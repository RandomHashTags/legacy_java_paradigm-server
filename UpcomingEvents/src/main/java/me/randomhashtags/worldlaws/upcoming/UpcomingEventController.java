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

public interface UpcomingEventController extends YouTubeService, Jsoupable, DataValues {
    HashMap<String, String> LOADED_PRE_UPCOMING_EVENTS = new HashMap<>();

    UpcomingEventType getType();
    default WLCountry getCountry() { // if null, it is worldwide/global
        return null;
    }
    HashMap<String, PreUpcomingEvent> getPreUpcomingEvents();
    HashMap<String, String> getUpcomingEvents();
    void load(CompletionHandler handler);

    default String getEventDateIdentifier(String dateString, String title) {
        final String id = LocalServer.fixEscapeValues(title).replaceAll("\\\\u[A-Fa-f\\d]{4}", "");
        return dateString + "." + id.replace(" ", "").replace("|", "_").replace("/", "-").replace(":", "-");
    }
    default String getEventDateString(EventDate date) {
        return getEventDateString(date.getYear(), date.getMonth(), date.getDay());
    }
    default String getEventDateString(int year, Month month, int day) {
        return month.getValue() + "-" + year + "-" + day;
    }
    default void getEventsFromDates(HashSet<String> dates, CompletionHandler handler) {
        final HashMap<String, PreUpcomingEvent> preUpcomingEvents = getPreUpcomingEvents();
        final HashMap<String, String> upcomingEvents = getUpcomingEvents();
        if(preUpcomingEvents == null && upcomingEvents == null) {
            final long started = System.currentTimeMillis();
            load(new CompletionHandler() {
                @Override
                public void handleString(String string) {
                    final HashMap<String, PreUpcomingEvent> newPreUpcomingEvents = getPreUpcomingEvents();
                    final HashMap<String, String> newUpcomingEvents = getUpcomingEvents();
                    final String preUpcomingEventsLoaded = newPreUpcomingEvents != null && !newPreUpcomingEvents.isEmpty() ? newPreUpcomingEvents.size() + " preUpcomingEvents" : null;
                    final String upcomingEventsLoaded = newUpcomingEvents != null && !newUpcomingEvents.isEmpty() ? newUpcomingEvents.size() + " upcomingEvents" : null;
                    final String amount = "(" + (preUpcomingEventsLoaded != null ? preUpcomingEventsLoaded + (upcomingEventsLoaded != null ? ", " : "") : "") + (upcomingEventsLoaded != null ? upcomingEventsLoaded : "") + ")";
                    WLLogger.log(Level.INFO, getType().name() + " - loaded " + amount + " events (took " + (System.currentTimeMillis()-started) + "ms)");
                    getEventsOnDates(dates, handler);
                }
            });
        } else {
            getEventsOnDates(dates, handler);
        }
    }
    private void getEventsOnDates(HashSet<String> dates, CompletionHandler handler) {
        final HashMap<String, PreUpcomingEvent> preUpcomingEvents = getPreUpcomingEvents();
        final HashSet<String> set = new HashSet<>((preUpcomingEvents != null ? preUpcomingEvents : getUpcomingEvents()).keySet());
        set.removeIf(id -> {
            for(String date : dates) {
                if(id.startsWith(date)) {
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
            set.parallelStream().forEach(id -> getPreUpcomingEvent(id, new CompletionHandler() {
                @Override
                public void handleString(String string) {
                    if(string != null) {
                        final String dateString = id.split("\\.")[0];
                        map.putIfAbsent(dateString, new HashSet<>());
                        map.get(dateString).add(string);
                    }
                    if(completed.addAndGet(1) == max) {
                        String value = null;
                        if(!map.isEmpty()) {
                            final StringBuilder builder = new StringBuilder("{");
                            boolean isFirstDateString = true;
                            for(Map.Entry<String, HashSet<String>> entry : map.entrySet()) {
                                builder.append(isFirstDateString ? "" : ",").append("\"").append(entry.getKey()).append("\":{");
                                final HashSet<String> events = entry.getValue();;
                                boolean isFirst = true;
                                for(String event : events) {
                                    builder.append(isFirst ? "" : ",").append(event);
                                    isFirst = false;
                                }
                                builder.append("}");
                                isFirstDateString = false;
                            }
                            builder.append("}");
                            value = builder.toString();
                        }
                        handler.handleStringValue(identifier, value);
                    }
                }
            }));
        }
    }
    private void getPreUpcomingEvent(String id, CompletionHandler handler) {
        if(LOADED_PRE_UPCOMING_EVENTS.containsKey(id)) {
            handler.handleString(LOADED_PRE_UPCOMING_EVENTS.get(id));
        } else {
            getUpcomingEvent(id, new CompletionHandler() {
                @Override
                public void handleString(String string) {
                    handler.handleString(LOADED_PRE_UPCOMING_EVENTS.get(id));
                }
            });
        }
    }
    default void saveUpcomingEventToJSON(String id, String json) {
        final Folder folder = Folder.UPCOMING_EVENTS_IDS;
        final String fileName = getUpcomingEventFileName(folder, id);
        setFileJSON(folder, fileName, json);
    }

    default void getResponse(String input, CompletionHandler handler) {
        getUpcomingEvent(input, handler);
    }
    default void getUpcomingEvent(String id, CompletionHandler handler) {
        final HashMap<String, String> upcomingEvents = getUpcomingEvents();
        if(upcomingEvents != null && upcomingEvents.containsKey(id)) {
            handler.handleString(upcomingEvents.get(id));
        } else {
            final Folder folder = Folder.UPCOMING_EVENTS_IDS;
            final String fileName = getUpcomingEventFileName(folder, id);
            getJSONObject(folder, fileName, new CompletionHandler() {
                @Override
                public void load(CompletionHandler handler) {
                    loadUpcomingEvent(id, handler);
                }

                @Override
                public void handleJSONObject(JSONObject json) {
                    String string = null;
                    if(json != null) {
                        string = toLoadedPreUpcomingEvent(id, json);
                    }
                    LOADED_PRE_UPCOMING_EVENTS.put(id, string);
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
        folder.setCustomFolderName(fileName, folder.getFolderName().replace("%year%", Integer.toString(year)).replace("%month%", month).replace("%day%", Integer.toString(day)));
        return fileName;
    }
    void loadUpcomingEvent(String id, CompletionHandler handler);

    private String toLoadedPreUpcomingEvent(String id, JSONObject json) {
        final HashMap<String, PreUpcomingEvent> preUpcomingEvents = getPreUpcomingEvents();
        final String imageURL = json.has("imageURL") ? json.getString("imageURL") : null;
        final PreUpcomingEvent preUpcomingEvent = preUpcomingEvents != null ? preUpcomingEvents.get(id) : PreUpcomingEvent.fromUpcomingEventJSON(getType(), id, json);
        return preUpcomingEvent.toStringWithImageURL(imageURL);
    }
}
