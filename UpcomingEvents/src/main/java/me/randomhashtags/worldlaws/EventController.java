package me.randomhashtags.worldlaws;

import me.randomhashtags.worldlaws.event.EventDate;
import me.randomhashtags.worldlaws.location.WLCountry;

import java.time.Month;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

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

    default String getEventIdentifier(EventDate date, String title) {
        return getEventDateIdentifier(date) + "." + title.toLowerCase().replace(" ", "").replace("|", "-");
    }
    default void getEventsFromDate(EventDate date, CompletionHandler handler) {
        final HashMap<String, String> events = getPreEvents();
        if(events == null || events.isEmpty()) {
            getUpcomingEvents(new CompletionHandler() {
                @Override
                public void handle(Object object) {
                    final HashMap<String, String> events = getPreEvents();
                    handler.handle(getEventsFromHashMap(date, events));
                }
            });
        } else {
            handler.handle(getEventsFromHashMap(date, events));
        }
    }
    private String getEventsFromHashMap(EventDate date, HashMap<String, String> hashmap) {
        final StringBuilder builder = new StringBuilder("[");
        if(hashmap != null) {
            final String identifier = getEventDateIdentifier(date) + ".";
            final HashMap<String, String> hashmapClone = new HashMap<>(hashmap);
            final Set<Map.Entry<String, String>> set = hashmapClone.entrySet();
            set.removeIf(map -> !map.getKey().startsWith(identifier));
            boolean isFirst = true;
            for(Map.Entry<String, String> value : set) {
                final String json = value.getValue();
                builder.append(isFirst ? "" : ",").append(json);
                isFirst = false;
            }
        }
        builder.append("]");
        return builder.toString();
    }
    private String getEventDateIdentifier(EventDate date) {
        return date.getMonth().getValue() + "-" + date.getYear() + "-" + date.getDay();
    }

    UpcomingEventType getType();
    default void getUpcomingEvents(@NotNull CompletionHandler handler) {
        final String cache = getCache();
        if(cache != null) {
            handler.handle(cache);
        } else {
            refresh(new CompletionHandler() {
                @Override
                public void handle(Object object) {
                    handler.handle(getCache());
                }
            });
        }
    }
    void refresh(CompletionHandler handler);
    String getCache();
    HashMap<String, String> getPreEvents();
    HashMap<String, String> getEvents();
    default String getUpcomingEvent(String identifier) {
        final HashMap<String, String> events = getEvents();
        return events != null ? events.getOrDefault(identifier, null) : null;
    }
    WLCountry getCountry(); // if null, it is worldwide/global

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
