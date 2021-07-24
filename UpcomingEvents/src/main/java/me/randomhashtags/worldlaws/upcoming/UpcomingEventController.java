package me.randomhashtags.worldlaws.upcoming;

import me.randomhashtags.worldlaws.*;
import me.randomhashtags.worldlaws.location.WLCountry;
import me.randomhashtags.worldlaws.service.YouTubeService;
import org.apache.logging.log4j.Level;
import org.json.JSONObject;

import java.time.LocalDate;
import java.time.Month;
import java.util.HashMap;
import java.util.HashSet;
import java.util.concurrent.atomic.AtomicInteger;

public interface UpcomingEventController extends YouTubeService, Jsoupable, DataValues {
    UpcomingEventType getType();
    default WLCountry getCountry() { // if null, it is worldwide/global
        return null;
    }
    HashMap<String, String> getLoadedPreUpcomingEvents();
    HashMap<String, PreUpcomingEvent> getPreUpcomingEvents();
    HashMap<String, String> getUpcomingEvents();
    void load(CompletionHandler handler);

    default String getEventDateIdentifier(String dateString, String title) {
        return dateString + "." + title.replace(" ", "").replace("|", "_").replace("/", "-");
    }
    default String getEventDateString(EventDate date) {
        return getEventDateString(date.getYear(), date.getMonth(), date.getDay());
    }
    default String getEventDateString(int year, Month month, int day) {
        return month.getValue() + "-" + year + "-" + day;
    }
    default void getEventsFromDate(EventDate date, CompletionHandler handler) {
        final String dateString = getEventDateString(date.getYear(), date.getMonth(), date.getDay());
        getEventsFromDate(dateString, handler);
    }
    default void getEventsFromDate(String date, CompletionHandler handler) {
        final HashMap<String, PreUpcomingEvent> preUpcomingEvents = getPreUpcomingEvents();
        final HashMap<String, String> upcomingEvents = getUpcomingEvents();
        if(preUpcomingEvents == null && upcomingEvents == null) {
            final long started = System.currentTimeMillis();
            load(new CompletionHandler() {
                @Override
                public void handleString(String string) {
                    final HashMap<String, PreUpcomingEvent> newPreUpcomingEvents = getPreUpcomingEvents();
                    final HashMap<String, String> newUpcomingEvents = getUpcomingEvents(), loadedPreUpcomingEvents = getLoadedPreUpcomingEvents();
                    final String preUpcomingEventsLoaded = newPreUpcomingEvents != null ? newPreUpcomingEvents.size() + " preUpcomingEvents" : "";
                    final String upcomingEventsLoaded = newUpcomingEvents != null ? newUpcomingEvents.size() + " upcomingEvents" : "";
                    final String loadedPreUpcomingEventsLoaded = loadedPreUpcomingEvents != null ? loadedPreUpcomingEvents.size() + " loadedPreUpcomingEvents" : "";
                    final boolean hasPreUpcomingEvents = !preUpcomingEventsLoaded.isEmpty(), hasUpcomingEvents = !upcomingEventsLoaded.isEmpty();
                    final String amount = "(" + preUpcomingEventsLoaded + (hasPreUpcomingEvents ? ", " : "") + upcomingEventsLoaded + (hasPreUpcomingEvents || hasUpcomingEvents ? ", " : "") + loadedPreUpcomingEventsLoaded + ")";
                    WLLogger.log(Level.INFO, getType().name() + " - loaded " + amount + " events (took " + (System.currentTimeMillis()-started) + "ms)");
                    getEventsOnDate(date, handler);
                }
            });
        } else {
            getEventsOnDate(date, handler);
        }
    }
    private void getEventsOnDate(String date, CompletionHandler handler) {
        final HashMap<String, PreUpcomingEvent> preUpcomingEvents = getPreUpcomingEvents();
        final HashSet<String> set = new HashSet<>((preUpcomingEvents != null ? preUpcomingEvents : getUpcomingEvents()).keySet());
        set.removeIf(id -> !id.startsWith(date + "."));
        final long max = set.size();
        if(max <= 0) {
            handler.handleString(null);
        } else {
            final HashSet<String> events = new HashSet<>();
            final AtomicInteger completed = new AtomicInteger(0);
            set.parallelStream().forEach(id -> getPreUpcomingEvent(id, new CompletionHandler() {
                @Override
                public void handleString(String string) {
                    if(string != null) {
                        events.add(string);
                    }
                    if(completed.addAndGet(1) == max) {
                        final StringBuilder builder = new StringBuilder("{");
                        boolean isFirst = true;
                        for(String event : events) {
                            builder.append(isFirst ? "" : ",").append(event);
                            isFirst = false;
                        }
                        builder.append("}");
                        final String value = builder.toString();
                        handler.handleString(value);
                    }
                }
            }));
        }
    }
    private void getPreUpcomingEvent(String id, CompletionHandler handler) {
        final HashMap<String, String> loadedPreUpcomingEvents = getLoadedPreUpcomingEvents();
        if(loadedPreUpcomingEvents.containsKey(id)) {
            handler.handleString(loadedPreUpcomingEvents.get(id));
        } else {
            getUpcomingEvent(id, new CompletionHandler() {
                @Override
                public void handleString(String string) {
                    handler.handleString(loadedPreUpcomingEvents.get(id));
                }
            });
        }
    }
    default void getUpcomingEvent(String id, CompletionHandler handler) {
        final HashMap<String, String> upcomingEvents = getUpcomingEvents();
        if(upcomingEvents.containsKey(id)) {
            handler.handleString(upcomingEvents.get(id));
        } else {
            final Folder folder = Folder.UPCOMING_EVENTS;
            final LocalDate now = WLUtilities.getNowUTC();
            final int year = now.getYear();
            final long epochDay = now.toEpochDay();
            folder.setCustomFolderName(folder.getFolderName(false).replace("%year%", Integer.toString(year)).replace("%day%", Long.toString(epochDay)));
            getJSONObject(folder, id, new CompletionHandler() {
                @Override
                public void load(CompletionHandler handler) {
                    loadUpcomingEvent(id, handler);
                }

                @Override
                public void handleJSONObject(JSONObject json) {
                    final PreUpcomingEvent preUpcomingEvent = getPreUpcomingEvents().get(id);
                    final String imageURL = json.has("imageURL") ? json.getString("imageURL") : null;
                    final String string = preUpcomingEvent.toStringWithImageURL(imageURL);
                    getLoadedPreUpcomingEvents().put(id, string);
                    handler.handleString(json.toString());
                }
            });
        }
    }
    void loadUpcomingEvent(String id, CompletionHandler handler);
}
