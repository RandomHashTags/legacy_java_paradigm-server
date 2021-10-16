package me.randomhashtags.worldlaws.upcoming;

import me.randomhashtags.worldlaws.*;
import me.randomhashtags.worldlaws.country.WLCountry;
import me.randomhashtags.worldlaws.service.YouTubeService;
import org.apache.logging.log4j.Level;
import org.json.JSONObject;

import java.time.Month;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class UpcomingEventController implements YouTubeService, Jsoupable, DataValues {
    protected static final ConcurrentHashMap<String, String> LOADED_PRE_UPCOMING_EVENTS = new ConcurrentHashMap<>();
    protected final HashMap<String, PreUpcomingEvent> preUpcomingEvents;
    protected final HashMap<String, String> upcomingEvents;

    public UpcomingEventController() {
        preUpcomingEvents = new HashMap<>();
        upcomingEvents = new HashMap<>();
    }

    public abstract UpcomingEventType getType();
    public WLCountry getCountry() { // if null, it is worldwide/global
        return null;
    }
    public abstract void load(CompletionHandler handler);

    public String getEventDateIdentifier(String dateString, String title) {
        final String id = LocalServer.fixEscapeValues(title).replaceAll("\\\\u[A-Fa-f\\d]{4}", "");
        return dateString + "." + id.replace(" ", "").replace("|", "_").replace("/", "-").replace(":", "-");
    }
    public String getEventDateString(EventDate date) {
        return getEventDateString(date.getYear(), date.getMonth(), date.getDay());
    }
    public String getEventDateString(int year, Month month, int day) {
        return month.getValue() + "-" + year + "-" + day;
    }
    public void getEventsFromDates(HashSet<String> dates, CompletionHandler handler) {
        if(preUpcomingEvents.isEmpty() && upcomingEvents.isEmpty()) {
            final long started = System.currentTimeMillis();
            load(new CompletionHandler() {
                @Override
                public void handleString(String string) {
                    final String preUpcomingEventsLoaded = !preUpcomingEvents.isEmpty() ? preUpcomingEvents.size() + " preUpcomingEvents" : null;
                    final String upcomingEventsLoaded = !upcomingEvents.isEmpty() ? upcomingEvents.size() + " upcomingEvents" : null;
                    String amount = "(" + (preUpcomingEventsLoaded != null ? preUpcomingEventsLoaded + (upcomingEventsLoaded != null ? ", " : "") : "") + (upcomingEventsLoaded != null ? upcomingEventsLoaded : "") + ")";
                    amount = amount.equals("()") ? "0" : amount;
                    WLLogger.log(Level.INFO, getType().name() + " - loaded " + amount + " events (took " + (System.currentTimeMillis()-started) + "ms)");
                    getEventsOnDates(dates, handler);
                }
            });
        } else {
            getEventsOnDates(dates, handler);
        }
    }
    private void getEventsOnDates(HashSet<String> dates, CompletionHandler handler) {
        final HashSet<String> set = new HashSet<>((!preUpcomingEvents.isEmpty() ? preUpcomingEvents : upcomingEvents).keySet());
        set.removeIf(id -> {
            for(String date : dates) {
                if(id.startsWith(date + ".")) {
                    return false;
                }
            }
            return true;
        });
        final String identifier = getType().name().toLowerCase();
        final int max = set.size();
        if(max == 0) {
            handler.handleStringValue(identifier, null);
        } else {
            final AtomicInteger completed = new AtomicInteger(0);
            final ConcurrentHashMap<String, HashSet<String>> map = new ConcurrentHashMap<>();
            final CompletionHandler completionHandler = new CompletionHandler() {
                @Override
                public void handleStringValue(String id, String value) {
                    if(id != null && value != null) {
                        final String dateString = id.split("\\.")[0];
                        map.putIfAbsent(dateString, new HashSet<>());
                        map.get(dateString).add(value);
                    }
                    if(completed.addAndGet(1) == max) {
                        String stringValue = null;
                        if(!map.isEmpty()) {
                            final StringBuilder builder = new StringBuilder("{");
                            boolean isFirstDateString = true;
                            for(Map.Entry<String, HashSet<String>> entry : map.entrySet()) {
                                builder.append(isFirstDateString ? "" : ",").append("\"").append(entry.getKey()).append("\":{");
                                final HashSet<String> events = entry.getValue();
                                boolean isFirst = true;
                                for(String event : events) {
                                    builder.append(isFirst ? "" : ",").append(event);
                                    isFirst = false;
                                }
                                builder.append("}");
                                isFirstDateString = false;
                            }
                            builder.append("}");
                            stringValue = builder.toString();
                        }
                        handler.handleStringValue(identifier, stringValue);
                    }
                }
            };
            set.parallelStream().forEach(id -> getPreUpcomingEvent(id, completionHandler));
        }
    }
    private void getPreUpcomingEvent(String id, CompletionHandler handler) {
        if(LOADED_PRE_UPCOMING_EVENTS.containsKey(id)) {
            handler.handleStringValue(id, LOADED_PRE_UPCOMING_EVENTS.get(id));
        } else {
            getUpcomingEvent(id, new CompletionHandler() {
                @Override
                public void handleString(String string) {
                    handler.handleStringValue(id, LOADED_PRE_UPCOMING_EVENTS.get(id));
                }
            });
        }
    }
    public void saveUpcomingEventToJSON(String id, String json) {
        final Folder folder = Folder.UPCOMING_EVENTS_IDS;
        final String fileName = getUpcomingEventFileName(folder, id);
        setFileJSON(folder, fileName, json);
    }

    public void getResponse(String input, CompletionHandler handler) {
        getUpcomingEvent(input, handler);
    }
    public void getUpcomingEvent(String id, CompletionHandler handler) {
        if(upcomingEvents.containsKey(id)) {
            handler.handleString(upcomingEvents.get(id));
        } else {
            final Folder folder = Folder.UPCOMING_EVENTS_IDS;
            final String fileName = getUpcomingEventFileName(folder, id);
            getJSONObject(folder, fileName, new CompletionHandler() {
                @Override
                public void load(CompletionHandler handler) {
                    tryLoadingUpcomingEvent(id, handler);
                }

                @Override
                public void handleJSONObject(JSONObject json) {
                    String string = null;
                    if(json != null) {
                        string = toLoadedPreUpcomingEvent(id, json);
                    }
                    if(string != null) {
                        LOADED_PRE_UPCOMING_EVENTS.put(id, string);
                    }
                    handler.handleString(json != null ? json.toString() : null);
                }
            });
        }
    }
    private String getUpcomingEventFileName(Folder folder, String id) {
        final String dateString = id.split("\\.")[0];
        final String[] values = dateString.split("-");
        final String month = Month.of(Integer.parseInt(values[0])).name();
        final int year = Integer.parseInt(values[1]), day = Integer.parseInt(values[2]);
        final String fileName = id.substring(dateString.length()+1);
        final String folderName = folder.getFolderName().replace("%year%", Integer.toString(year)).replace("%month%", month).replace("%day%", Integer.toString(day));
        folder.setCustomFolderName(fileName, folderName);
        return fileName;
    }
    private void tryLoadingUpcomingEvent(String id, CompletionHandler handler) {
        if(preUpcomingEvents.containsKey(id)) {
            loadUpcomingEvent(id, handler);
        } else {
            handler.handleString(null);
        }
    }
    public abstract void loadUpcomingEvent(String id, CompletionHandler handler);

    private String toLoadedPreUpcomingEvent(String id, JSONObject json) {
        final UpcomingEventType type = getType();
        final String imageURL = json.has("imageURL") ? json.getString("imageURL") : null;
        final PreUpcomingEvent preUpcomingEvent = preUpcomingEvents.getOrDefault(id, PreUpcomingEvent.fromUpcomingEventJSON(type, id, json));
        return preUpcomingEvent.toStringWithImageURL(getType(), imageURL);
    }
}
