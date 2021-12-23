package me.randomhashtags.worldlaws.upcoming;

import me.randomhashtags.worldlaws.*;
import me.randomhashtags.worldlaws.country.WLCountry;
import me.randomhashtags.worldlaws.service.YouTubeService;
import me.randomhashtags.worldlaws.stream.ParallelStream;
import org.json.JSONObject;

import java.time.LocalDate;
import java.time.Month;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public abstract class UpcomingEventController implements YouTubeService, Jsoupable, DataValues {
    private final ConcurrentHashMap<String, String> loadedPreUpcomingEvents, upcomingEvents;
    private final ConcurrentHashMap<String, PreUpcomingEvent> preUpcomingEvents;

    public UpcomingEventController() {
        loadedPreUpcomingEvents = new ConcurrentHashMap<>();
        preUpcomingEvents = new ConcurrentHashMap<>();
        upcomingEvents = new ConcurrentHashMap<>();
    }

    public abstract UpcomingEventType getType();
    public WLCountry getCountry() { // if null, it is worldwide/global
        return null;
    }

    public void refresh(CompletionHandler handler) {
        final long started = System.currentTimeMillis();
        loadedPreUpcomingEvents.clear();
        preUpcomingEvents.clear();
        upcomingEvents.clear();
        try {
            load(new CompletionHandler() {
                @Override
                public void handleString(String string) {
                    final String preUpcomingEventsLoaded = !preUpcomingEvents.isEmpty() ? preUpcomingEvents.size() + " preUpcomingEvents" : null;
                    final String upcomingEventsLoaded = !upcomingEvents.isEmpty() ? upcomingEvents.size() + " upcomingEvents" : null;
                    String amount = "(" + (preUpcomingEventsLoaded != null ? preUpcomingEventsLoaded + (upcomingEventsLoaded != null ? ", " : "") : "") + (upcomingEventsLoaded != null ? upcomingEventsLoaded : "") + ")";
                    amount = amount.equals("()") ? "0" : amount;
                    WLLogger.logInfo(getType().name() + " - loaded " + amount + " events (took " + (System.currentTimeMillis()-started) + "ms)");
                    handler.handleString(string);
                }
            });
        } catch (Exception e) {
            WLUtilities.saveException(e);
            handler.handleString(null);
        }
    }
    public abstract void load(CompletionHandler handler);

    public String getEventDateIdentifier(String dateString, String title) {
        final String id = LocalServer.fixEscapeValues(title)
                .replaceAll("\\\\u[A-Fa-f\\d]{4}", "")
                .replaceAll("[^\\w-]", "_");
        return dateString + "." + id;
    }
    public String getEventDateString(EventDate date) {
        return getEventDateString(date.getYear(), date.getMonth(), date.getDay());
    }
    public String getEventDateString(int year, Month month, int day) {
        return month.getValue() + "-" + year + "-" + day;
    }
    public void getEventsFromDates(HashSet<String> dates, CompletionHandler handler) {
        if(preUpcomingEvents.isEmpty() && upcomingEvents.isEmpty()) {
            refresh(new CompletionHandler() {
                @Override
                public void handleString(String string) {
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
            final String dateString = id.split("\\.")[0];
            return !dates.contains(dateString);
        });
        final String typeIdentifier = getType().name().toLowerCase();
        String stringValue = null;
        if(set.size() > 0) {
            final ConcurrentHashMap<String, HashSet<String>> map = new ConcurrentHashMap<>();
            final CompletionHandler completionHandler = new CompletionHandler() {
                @Override
                public void handleStringValue(String id, String value) {
                    if(id != null && value != null) {
                        final String dateString = id.split("\\.")[0];
                        map.putIfAbsent(dateString, new HashSet<>());
                        map.get(dateString).add(value);
                    }
                }
            };
            ParallelStream.stream(set, identifier -> getPreUpcomingEvent((String) identifier, completionHandler));

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
        }
        handler.handleStringValue(typeIdentifier, stringValue);
    }
    private void getPreUpcomingEvent(String identifier, CompletionHandler handler) {
        if(loadedPreUpcomingEvents.containsKey(identifier)) {
            handler.handleStringValue(identifier, loadedPreUpcomingEvents.get(identifier));
        } else {
            getUpcomingEvent(identifier, new CompletionHandler() {
                @Override
                public void handleString(String string) {
                    handler.handleStringValue(identifier, loadedPreUpcomingEvents.get(identifier));
                }
            });
        }
    }
    public void saveUpcomingEventToJSON(String id, String json) {
        new Thread(() -> {
            final Folder folder = Folder.UPCOMING_EVENTS_IDS;
            final String fileName = getUpcomingEventFileName(folder, id);
            setFileJSON(folder, fileName, json);
        }).start();
    }

    public void getResponse(String input, CompletionHandler handler) {
        getUpcomingEvent(input, handler);
    }
    public void getUpcomingEvent(String identifier, CompletionHandler handler) {
        if(upcomingEvents.containsKey(identifier)) {
            final String string = upcomingEvents.get(identifier);
            handler.handleString(string.isEmpty() ? null : string);
        } else {
            final Folder folder = Folder.UPCOMING_EVENTS_IDS;
            final String fileName = getUpcomingEventFileName(folder, identifier);
            final String string = getLocalFileString(folder, fileName, "json");
            if(string != null) {
                handler.handleString(string);
            } else {
                final String todayEventDateString = new EventDate(LocalDate.now()).getDateString() + ".";
                tryLoadingUpcomingEvent(identifier, new CompletionHandler() {
                    @Override
                    public void handleString(String string) {
                        final JSONObject json = string != null && string.startsWith("{") ? new JSONObject(string) : null;
                        handleJSONObject(json);
                    }

                    @Override
                    public void handleJSONObject(JSONObject json) {
                        String string = null;
                        final String jsonString = json != null ? json.toString() : "";
                        if(json != null) {
                            string = toLoadedPreUpcomingEvent(identifier, json);
                        }
                        if(string != null) {
                            putLoadedPreUpcomingEvent(identifier, string);
                        }
                        if(!jsonString.isEmpty() && identifier.startsWith(todayEventDateString)) {
                            saveUpcomingEventToJSON(identifier, jsonString);
                        }
                        upcomingEvents.put(identifier, jsonString);
                        handler.handleString(jsonString);
                    }
                });
            }
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

    public void putPreUpcomingEvent(String identifier, PreUpcomingEvent event) {
        preUpcomingEvents.put(identifier, event);
    }
    public PreUpcomingEvent getPreUpcomingEvent(String identifier) {
        return preUpcomingEvents.get(identifier);
    }

    public void putUpcomingEvent(String identifier, String value) {
        upcomingEvents.put(identifier, value);
    }

    public void putLoadedPreUpcomingEvent(String identifier, String value) {
        loadedPreUpcomingEvents.put(identifier, value);
    }
}
