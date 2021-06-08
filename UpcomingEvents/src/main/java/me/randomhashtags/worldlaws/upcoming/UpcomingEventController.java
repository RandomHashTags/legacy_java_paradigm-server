package me.randomhashtags.worldlaws.upcoming;

import me.randomhashtags.worldlaws.*;
import me.randomhashtags.worldlaws.location.WLCountry;
import org.apache.logging.log4j.Level;

import java.time.Month;
import java.util.HashMap;
import java.util.HashSet;
import java.util.concurrent.atomic.AtomicInteger;

public interface UpcomingEventController extends RestAPI, Jsoupable {
    UpcomingEventType getType();
    WLCountry getCountry(); // if null, it is worldwide/global
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
                public void handle(Object object) {
                    final HashMap<String, PreUpcomingEvent> newPreUpcomingEvents = getPreUpcomingEvents();
                    final HashMap<String, String> newUpcomingEvents = getUpcomingEvents(), loadedPreUpcomingEvents = getLoadedPreUpcomingEvents();
                    final String amount = "(" + (newPreUpcomingEvents != null ? newPreUpcomingEvents.size() + " preUpcomingEvents, " : "") + newUpcomingEvents.size() + " upcoming events, " + loadedPreUpcomingEvents.size() + " loadedPreUpcomingEvents)";
                    WLLogger.log(Level.INFO, getType().name() + " - loaded " + amount + " events (took " + (System.currentTimeMillis()-started) + "ms)");
                    getEventsOnDate(date, handler);
                }
            });
        } else {
            getEventsOnDate(date, handler);
        }
    }
    private void getEventsOnDate(String date, CompletionHandler handler) {
        final long started = System.currentTimeMillis();
        final HashMap<String, PreUpcomingEvent> preUpcomingEvents = getPreUpcomingEvents();
        final HashSet<String> set = new HashSet<>((preUpcomingEvents != null ? preUpcomingEvents : getUpcomingEvents()).keySet());
        set.removeIf(id -> !id.startsWith(date + "."));
        final long max = set.size();
        if(max <= 0) {
            handler.handle("{}");
        } else {
            final HashSet<String> events = new HashSet<>();
            final AtomicInteger completed = new AtomicInteger(0);
            set.parallelStream().forEach(id -> {
                getPreUpcomingEvent(id, new CompletionHandler() {
                    @Override
                    public void handle(Object object) {
                        if(object != null) {
                            events.add(object.toString());
                        }
                        if(completed.addAndGet(1) == max) {
                            final StringBuilder builder = new StringBuilder("{");
                            boolean isFirst = true;
                            for(String event : events) {
                                builder.append(isFirst ? "" : ",").append(event);
                                isFirst = false;
                            }
                            builder.append("}");
                            final String string = builder.toString();
                            WLLogger.log(Level.INFO, getType().name() + " - loaded events for date \"" + date + "\" (took " + (System.currentTimeMillis()-started) + "ms)");
                            handler.handle(string);
                        }
                    }
                });
            });
        }
    }
    private void getPreUpcomingEvent(String id, CompletionHandler handler) {
        final HashMap<String, String> loadedPreUpcomingEvents = getLoadedPreUpcomingEvents();
        if(loadedPreUpcomingEvents.containsKey(id)) {
            handler.handle(loadedPreUpcomingEvents.get(id));
        } else {
            getUpcomingEvent(id, new CompletionHandler() {
                @Override
                public void handle(Object object) {
                    handler.handle(loadedPreUpcomingEvents.get(id));
                }
            });
        }
    }
    default void getUpcomingEvent(String id, CompletionHandler handler) {
        final HashMap<String, String> upcomingEvents = getUpcomingEvents();
        if(upcomingEvents.containsKey(id)) {
            handler.handle(upcomingEvents.get(id));
        } else {
            loadUpcomingEvent(id, handler);
        }
    }
    void loadUpcomingEvent(String id, CompletionHandler handler);
}
