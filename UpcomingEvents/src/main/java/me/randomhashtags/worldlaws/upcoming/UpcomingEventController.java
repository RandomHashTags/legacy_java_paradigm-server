package me.randomhashtags.worldlaws.upcoming;

import me.randomhashtags.worldlaws.*;
import me.randomhashtags.worldlaws.country.WLCountry;
import me.randomhashtags.worldlaws.past.science.ScienceYearReview;
import me.randomhashtags.worldlaws.service.YouTubeService;
import me.randomhashtags.worldlaws.stream.CompletableFutures;
import me.randomhashtags.worldlaws.upcoming.entertainment.TVShows;
import org.json.JSONObject;

import java.io.File;
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

    public void refresh() {
        final long started = System.currentTimeMillis();
        loadedPreUpcomingEvents.clear();
        preUpcomingEvents.clear();
        upcomingEvents.clear();
        try {
            load();
            final String preUpcomingEventsLoaded = !preUpcomingEvents.isEmpty() ? preUpcomingEvents.size() + " preUpcomingEvents" : null;
            final String upcomingEventsLoaded = !upcomingEvents.isEmpty() ? upcomingEvents.size() + " upcomingEvents" : null;
            String amount = "(" + (preUpcomingEventsLoaded != null ? preUpcomingEventsLoaded + (upcomingEventsLoaded != null ? ", " : "") : "") + (upcomingEventsLoaded != null ? upcomingEventsLoaded : "") + ")";
            amount = amount.equals("()") ? "0" : amount;
            WLLogger.logInfo(getType().name() + " - loaded " + amount + " events (took " + WLUtilities.getElapsedTime(started) + ")");
        } catch (Exception e) {
            WLUtilities.saveException(e);
        }
    }
    public abstract void load();

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
    public String getEventsFromDates(HashSet<String> dates) {
        if(preUpcomingEvents.isEmpty() && upcomingEvents.isEmpty()) {
            refresh();
        }
        return getEventsOnDates(dates);
    }
    private String getEventsOnDates(HashSet<String> dates) {
        final HashSet<String> set = new HashSet<>((!preUpcomingEvents.isEmpty() ? preUpcomingEvents : upcomingEvents).keySet());
        set.removeIf(id -> {
            final String dateString = id.split("\\.")[0];
            return !dates.contains(dateString);
        });
        final ConcurrentHashMap<String, HashSet<String>> map = new ConcurrentHashMap<>();
        if(set.size() > 0) {
            new CompletableFutures<String>().stream(set, identifier -> {
                final String string = getLoadedPreUpcomingEvent(identifier);
                if(string != null) {
                    final String dateString = identifier.split("\\.")[0];
                    map.putIfAbsent(dateString, new HashSet<>());
                    map.get(dateString).add(string);
                }
            });
        } else if(!loadedPreUpcomingEvents.isEmpty()) {
            new CompletableFutures<String>().stream(loadedPreUpcomingEvents.keySet(), identifier -> {
                final String string = loadedPreUpcomingEvents.get(identifier);
                final String dateString = identifier.split("\\.")[0];
                map.putIfAbsent(dateString, new HashSet<>());
                map.get(dateString).add(string);
            });
        }
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
        return stringValue;
    }
    private String getLoadedPreUpcomingEvent(String identifier) {
        if(!loadedPreUpcomingEvents.containsKey(identifier)) {
            final String string = getUpcomingEvent(identifier);
        }
        return loadedPreUpcomingEvents.get(identifier);
    }
    public void saveUpcomingEventToJSON(String id, String json) {
        final Folder folder = Folder.UPCOMING_EVENTS_IDS;
        final String fileName = getUpcomingEventFileName(folder, id);
        final File file = folder.literalFileExists(fileName, fileName + ".json");
        if(file == null) {
            setFileJSON(folder, fileName, json);
        }
    }

    public String getResponse(String input) {
        return getUpcomingEvent(input);
    }
    public String getUpcomingEvent(String identifier) {
        if(upcomingEvents.containsKey(identifier)) {
            final String string = upcomingEvents.get(identifier);
            return string.isEmpty() ? null : string;
        } else {
            final Folder folder = Folder.UPCOMING_EVENTS_IDS;
            final String fileName = getUpcomingEventFileName(folder, identifier);
            String string = getLocalFileString(folder, fileName, "json");
            if(string == null) {
                final JSONObject json = tryLoadingUpcomingEvent(identifier);
                String value = null;
                final String jsonString = json != null ? json.toString() : "";
                if(json != null) {
                    value = toLoadedPreUpcomingEvent(identifier, json);
                }
                if(value != null) {
                    putLoadedPreUpcomingEvent(identifier, value);
                }
                if(!jsonString.isEmpty()) {
                    final String todayEventDateString = EventDate.getDateString(LocalDate.now()) + ".";
                    if(identifier.startsWith(todayEventDateString)) {
                        saveUpcomingEventToJSON(identifier, jsonString);
                    }
                }
                upcomingEvents.put(identifier, jsonString);
                return jsonString;
            }
            upcomingEvents.put(identifier, string);
            return string;
        }
    }
    private String getUpcomingEventFileName(Folder folder, String id) {
        final String dateString = id.split("\\.")[0];
        final String[] values = dateString.split("-");
        final String month = Month.of(Integer.parseInt(values[0])).name();
        final int year = Integer.parseInt(values[1]), day = Integer.parseInt(values[2]);
        final String fileName = id.substring(dateString.length()+1);
        final String folderName = folder.getFolderName()
                .replace("%year%", Integer.toString(year))
                .replace("%month%", month)
                .replace("%day%", Integer.toString(day))
                .replace("%type%", getType().getID())
                ;
        folder.setCustomFolderName(fileName, folderName);
        return fileName;
    }
    private JSONObject tryLoadingUpcomingEvent(String id) {
        if(preUpcomingEvents.containsKey(id)) {
            final String string = loadUpcomingEvent(id);
            if(string != null && string.startsWith("{") && string.endsWith("}")) {
                return new JSONObject(string);
            }
        }
        return null;
    }
    public abstract String loadUpcomingEvent(String id);

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
        if(this instanceof TVShows || this instanceof ScienceYearReview) {
        } else {
            final String todayDateString = EventDate.getDateString(LocalDate.now()) + ".";
            if(identifier.startsWith(todayDateString)) {
                saveUpcomingEventToJSON(identifier, value);
            }
        }
        upcomingEvents.put(identifier, value);
    }

    public void putLoadedPreUpcomingEvent(String identifier, String value) {
        loadedPreUpcomingEvents.put(identifier, value);
    }

    public ConcurrentHashMap<String, PreUpcomingEvent> getPreUpcomingEvents() {
        return preUpcomingEvents;
    }
    public ConcurrentHashMap<String, String> getLoadedPreUpcomingEvents() {
        return loadedPreUpcomingEvents;
    }
    public ConcurrentHashMap<String, String> getUpcomingEvents() {
        return upcomingEvents;
    }
}
