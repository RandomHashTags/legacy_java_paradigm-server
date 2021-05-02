package me.randomhashtags.worldlaws;

import me.randomhashtags.worldlaws.location.WLCountry;
import org.apache.logging.log4j.Level;

import java.time.Month;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

public interface EventController extends RestAPI, Jsoupable {
    HashMap<String, Month> MONTHS = monthMap();

    static HashMap<String, Month> monthMap() {
        final HashMap<String, Month> months = new HashMap<>();
        for(Month month : Month.values()) {
            String target = "";
            final String name = month.name();
            for(int i = 0; i < name.length(); i++) {
                final String string = Character.toString(name.charAt(i));
                target = target.concat(string).concat(" ");
            }
            months.put(target, month);
        }
        return months;
    }

    UpcomingEventType getType();
    WLCountry getCountry(); // if null, it is worldwide/global
    HashMap<String, String> getPreUpcomingEvents();
    HashMap<String, NewPreUpcomingEvent> getPreEventURLs();
    HashMap<String, String> getUpcomingEvents();
    void load(CompletionHandler handler);

    default void getEventsFromDate(EventDate date, CompletionHandler handler) {
        final String dateString = date.getMonth().getValue() + "-" + date.getYear() + "-" + date.getDay();
        getEventsFromDate(dateString, handler);
    }
    default void getEventsFromDate(String date, CompletionHandler handler) {
        final HashMap<String, NewPreUpcomingEvent> preEventURLS = getPreEventURLs();
        final HashMap<String, String> upcomingEvents = getUpcomingEvents();
        if(preEventURLS == null && upcomingEvents == null) {
            final long started = System.currentTimeMillis();
            load(new CompletionHandler() {
                @Override
                public void handle(Object object) {
                    WLLogger.log(Level.INFO, getType().name() + " - loaded (took " + (System.currentTimeMillis()-started) + "ms)");
                    getEventsOnDate(date, handler);
                }
            });
        } else {
            getEventsOnDate(date, handler);
        }
    }
    private void getEventsOnDate(String date, CompletionHandler handler) {
        final long started = System.currentTimeMillis();
        final HashSet<String> eventDates = new HashSet<>();
        final HashMap<String, NewPreUpcomingEvent> preEventURLs = getPreEventURLs();
        final Set<String> set = (preEventURLs == null ? getUpcomingEvents() : preEventURLs).keySet();
        for(String id : set) {
            if(id.startsWith(date + ".")) {
                eventDates.add(id);
            }
        }
        final long max = eventDates.size();
        if(max == 0) {
            handler.handle("[]");
        } else {
            final HashSet<String> events = new HashSet<>();
            final AtomicInteger completed = new AtomicInteger(0);
            eventDates.parallelStream().forEach(id -> {
                getPreUpcomingEvent(id, new CompletionHandler() {
                    @Override
                    public void handle(Object object) {
                        final int value = completed.addAndGet(1);
                        if(object != null) {
                            events.add(object.toString());
                        }
                        if(value == max) {
                            final StringBuilder builder = new StringBuilder("[");
                            boolean isFirst = true;
                            for(String event : events) {
                                builder.append(isFirst ? "" : ",").append(event);
                                isFirst = false;
                            }
                            builder.append("]");
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
        final HashMap<String, String> preUpcomingEvents = getPreUpcomingEvents();
        if(preUpcomingEvents.containsKey(id)) {
            handler.handle(preUpcomingEvents.get(id));
        } else {
            getUpcomingEvent(id, new CompletionHandler() {
                @Override
                public void handle(Object object) {
                    handler.handle(preUpcomingEvents.get(id));
                }
            });
        }
    }
    void getUpcomingEvent(String id, CompletionHandler handler);

    default Month getMonthFrom(String text, Set<String> keys) {
        for(String key : keys) {
            if(text.startsWith(key)) {
                return MONTHS.get(key);
            }
        }
        return null;
    }
    default String getMonthKey(Month month, Set<String> keys) {
        for(String key : keys) {
            if(MONTHS.get(key).equals(month)) {
                return key;
            }
        }
        return null;
    }
}
