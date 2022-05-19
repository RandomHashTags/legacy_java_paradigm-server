package me.randomhashtags.worldlaws.upcoming;

import me.randomhashtags.worldlaws.*;
import me.randomhashtags.worldlaws.country.WLCountry;
import me.randomhashtags.worldlaws.locale.JSONObjectTranslatable;
import me.randomhashtags.worldlaws.service.YouTubeService;
import me.randomhashtags.worldlaws.stream.CompletableFutures;
import me.randomhashtags.worldlaws.upcoming.entertainment.TVShows;
import me.randomhashtags.worldlaws.upcoming.events.LoadedPreUpcomingEvent;
import me.randomhashtags.worldlaws.upcoming.events.UpcomingEvent;
import org.json.JSONObject;

import java.io.File;
import java.time.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public abstract class UpcomingEventController implements YouTubeService, Jsoupable, DataValues {
    private final ConcurrentHashMap<String, LoadedPreUpcomingEvent> loadedPreUpcomingEvents;
    private final ConcurrentHashMap<String, UpcomingEvent> upcomingEvents;
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
        boolean didError = false;
        try {
            load();
        } catch (Exception e) {
            WLUtilities.saveException(e);
            didError = true;
        }
        final String preUpcomingEventsLoaded = !preUpcomingEvents.isEmpty() ? preUpcomingEvents.size() + " preUpcomingEvents" : null;
        final String upcomingEventsLoaded = !upcomingEvents.isEmpty() ? upcomingEvents.size() + " upcomingEvents" : null;
        final String loadedPreUpcomingEventsLoaded = !loadedPreUpcomingEvents.isEmpty() ? loadedPreUpcomingEvents.size() + " loadedPreUpcomingEvents" : null;
        final StringBuilder builder = new StringBuilder();
        if(preUpcomingEventsLoaded != null) {
            builder.append(preUpcomingEventsLoaded);
        }
        if(upcomingEventsLoaded != null) {
            builder.append(builder.length() > 0 ? ", " : "").append(upcomingEventsLoaded);
        }
        if(loadedPreUpcomingEventsLoaded != null) {
            builder.append(builder.length() > 0 ? ", " : "").append(loadedPreUpcomingEventsLoaded);
        }
        if(builder.length() > 0) {
            builder.insert(0, "(").append(")");
        } else {
            builder.append("0");
        }
        WLLogger.logInfo(getType().name() + " - loaded " + builder.toString() + " events (took " + WLUtilities.getElapsedTime(started) + ")" + (didError ? " [DID ENCOUNTER ERROR LOADING]" : ""));
    }
    public abstract void load();

    public boolean isExactTime() {
        return false;
    }

    public static String getEventDateIdentifier(String dateString, String title) {
        return parseEventDateIdentifier(dateString, title);
    }
    public static String getEventDateIdentifier(long exactTimeMilliseconds, String title) {
        return parseEventDateIdentifier(exactTimeMilliseconds + "", title);
    }
    private static String parseEventDateIdentifier(String key, String title) {
        final String escaped = LocalServer.fixEscapeValues(title);
        final String id = escaped != null ? escaped
                .replaceAll("\\\\u[A-Fa-f\\d]{4}", "")
                .replaceAll("[^\\w-]", "_")
                : null;
        return key + "." + id;
    }
    public ConcurrentHashMap<String, Collection<LoadedPreUpcomingEvent>> getEventsFromDates(HashSet<String> dates) {
        final HashSet<String> set = new HashSet<>((!preUpcomingEvents.isEmpty() ? preUpcomingEvents : upcomingEvents).keySet());
        set.removeIf(id -> {
            final String dateString = id.split("\\.")[0];
            return !dates.contains(dateString);
        });
        final ConcurrentHashMap<String, Collection<LoadedPreUpcomingEvent>> map = new ConcurrentHashMap<>();
        if(set.size() > 0) {
            final ConcurrentHashMap<String, HashMap<String, LoadedPreUpcomingEvent>> events = new ConcurrentHashMap<>();
            new CompletableFutures<String>().stream(set, identifier -> {
                final LoadedPreUpcomingEvent event = getLoadedPreUpcomingEvent(identifier);
                if(event != null) {
                    final String dateString = identifier.split("\\.")[0];
                    events.putIfAbsent(dateString, new HashMap<>());
                    events.get(dateString).put(identifier, event);
                }
            });
            for(Map.Entry<String, HashMap<String, LoadedPreUpcomingEvent>> entry : events.entrySet()) {
                map.put(entry.getKey(), entry.getValue().values());
            }
        } else if(!loadedPreUpcomingEvents.isEmpty()) {
            final HashSet<String> events = new HashSet<>(loadedPreUpcomingEvents.keySet());
            events.removeIf(id -> {
                final String dateString = id.split("\\.")[0];
                return !dates.contains(dateString);
            });
            for(String identifier : events) {
                final LoadedPreUpcomingEvent event = loadedPreUpcomingEvents.get(identifier);
                final String dateString = identifier.split("\\.")[0];
                map.putIfAbsent(dateString, new ArrayList<>());
                map.get(dateString).add(event);
            }
        }
        return map.isEmpty() ? null : map;
    }
    public Collection<LoadedPreUpcomingEvent> getExactTimeEvents() {
        final HashSet<String> set = new HashSet<>((!preUpcomingEvents.isEmpty() ? preUpcomingEvents : upcomingEvents).keySet());
        set.removeIf(id -> {
            final String key = id.split("\\.")[0];
            return !key.matches("[0-9]+");
        });
        final HashSet<LoadedPreUpcomingEvent> events = new HashSet<>();
        if(set.size() > 0) {
            new CompletableFutures<String>().stream(set, identifier -> {
                final LoadedPreUpcomingEvent event = getLoadedPreUpcomingEvent(identifier);
                if(event != null) {
                    events.add(event);
                }
            });
        } else if(!loadedPreUpcomingEvents.isEmpty()) {
            final HashSet<String> copy = new HashSet<>(loadedPreUpcomingEvents.keySet());
            copy.removeIf(identifier -> {
                final String key = identifier.split("\\.")[0];
                return !key.matches("[0-9]+");
            });
            for(String identifier : copy) {
                events.add(loadedPreUpcomingEvents.get(identifier));
            }
        }
        return events.isEmpty() ? null : events;
    }
    private LoadedPreUpcomingEvent getLoadedPreUpcomingEvent(String identifier) {
        if(!loadedPreUpcomingEvents.containsKey(identifier)) {
            final JSONObjectTranslatable string = getUpcomingEvent(identifier);
        }
        return loadedPreUpcomingEvents.get(identifier);
    }
    public void saveUpcomingEventToJSON(String id, String json) {
        final Folder folder = Folder.UPCOMING_EVENTS_IDS;
        final String fileName = getUpcomingEventFileName(getType(), folder, id);
        final String test = Jsonable.getFilePath(folder, fileName, "json");
        final File file = folder.literalFileExists(test);
        if(file == null) {
            setFileJSON(folder, fileName, json);
        }
    }

    public UpcomingEvent getUpcomingEvent(String identifier) {
        if(upcomingEvents.containsKey(identifier)) {
            final UpcomingEvent event = upcomingEvents.get(identifier);
            return event.isEmpty() ? null : event;
        } else {
            final Folder folder = Folder.UPCOMING_EVENTS_IDS;
            final String fileName = getUpcomingEventFileName(getType(), folder, identifier);
            String string = getLocalFileString(folder, fileName, "json");
            final UpcomingEvent event;
            if(string == null) {
                event = tryLoadingUpcomingEvent(identifier);
                if(event != null) {
                    trySaving(event);
                }
            } else {
                final JSONObject json = new JSONObject(string);
                event = parseUpcomingEvent(json);
                if(event != null) {
                    event.insertProperties();
                }
            }
            if(event != null) {
                final LoadedPreUpcomingEvent value = toLoadedPreUpcomingEvent(identifier, event);
                putLoadedPreUpcomingEvent(value);
                upcomingEvents.put(identifier, event);
            }
            return event;
        }
    }
    public static String getUpcomingEventFileName(UpcomingEventType type, Folder folder, String identifier) {
        final String dateString = identifier.split("\\.")[0];
        final String month;
        final int year, day;
        if(dateString.matches("[0-9]+")) {
            final ZonedDateTime instant = Instant.ofEpochMilli(Long.parseLong(dateString)).atZone(ZoneId.systemDefault());
            month = instant.getMonth().name();
            year = instant.getYear();
            day = instant.getDayOfMonth();
        } else {
            final String[] values = dateString.split("-");
            month = Month.of(Integer.parseInt(values[0])).name();
            year = Integer.parseInt(values[1]);
            day = Integer.parseInt(values[2]);
        }

        final String fileName = identifier.substring(dateString.length()+1);
        final String folderName = folder.getFolderName()
                .replace("%year%", Integer.toString(year))
                .replace("%month%", month)
                .replace("%day%", Integer.toString(day))
                .replace("%type%", type.getID())
                ;
        folder.setCustomFolderName(fileName, folderName);
        return fileName;
    }
    private UpcomingEvent tryLoadingUpcomingEvent(String id) {
        if(preUpcomingEvents.containsKey(id)) {
            final UpcomingEvent event = loadUpcomingEvent(id);
            if(event != null) {
                putUpcomingEvent(id, event);
                return event;
            }
        }
        return null;
    }
    public abstract UpcomingEvent loadUpcomingEvent(String id);
    public abstract UpcomingEvent parseUpcomingEvent(JSONObject json);

    private LoadedPreUpcomingEvent toLoadedPreUpcomingEvent(String id, JSONObject json) {
        final UpcomingEventType type = getType();
        final String imageURL = json.optString("imageURL", null);
        final PreUpcomingEvent preUpcomingEvent = preUpcomingEvents.getOrDefault(id, PreUpcomingEvent.fromUpcomingEventJSON(type, id, json));
        return preUpcomingEvent.toLoadedPreUpcomingEventWithImageURL(getType(), imageURL);
    }

    public void putPreUpcomingEvent(String identifier, PreUpcomingEvent event) {
        preUpcomingEvents.put(identifier, event);
    }
    public PreUpcomingEvent getPreUpcomingEvent(String identifier) {
        return preUpcomingEvents.get(identifier);
    }

    public void putUpcomingEvent(String identifier, UpcomingEvent value) {
        trySaving(value);
        upcomingEvents.put(identifier, value);
    }

    private void trySaving(UpcomingEvent event) {
        if(this instanceof TVShows) {
        } else {
            final String identifier = event.getIdentifier();
            if(event.getDate() != null) {
                final String todayDateString = EventDate.getDateString(LocalDate.now()) + ".";
                if(identifier.startsWith(todayDateString)) {
                    saveUpcomingEventToJSON(identifier, event.toString());
                }
            } else {
                final long instant = WLUtilities.getTodayStartOfDayMilliseconds();
                final long exactTimeMilliseconds = event.getExactTimeMilliseconds();
                if(exactTimeMilliseconds > instant && exactTimeMilliseconds < instant + TimeUnit.DAYS.toMillis(1)) {
                    saveUpcomingEventToJSON(identifier, event.toString());
                }
            }
        }
    }

    public void putLoadedPreUpcomingEvent(LoadedPreUpcomingEvent value) {
        if(value != null) {
            loadedPreUpcomingEvents.put(value.getIdentifier(), value);
        }
    }

    public ConcurrentHashMap<String, PreUpcomingEvent> getPreUpcomingEvents() {
        return preUpcomingEvents;
    }
    public ConcurrentHashMap<String, LoadedPreUpcomingEvent> getLoadedPreUpcomingEvents() {
        return loadedPreUpcomingEvents;
    }
    public ConcurrentHashMap<String, UpcomingEvent> getUpcomingEvents() {
        return upcomingEvents;
    }
}
