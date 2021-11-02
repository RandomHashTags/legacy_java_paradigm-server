package me.randomhashtags.worldlaws;

import me.randomhashtags.worldlaws.recent.PreRecentEvent;
import me.randomhashtags.worldlaws.recent.RecentEventController;
import me.randomhashtags.worldlaws.recent.RecentEventType;
import me.randomhashtags.worldlaws.recent.VideoGameUpdates;
import me.randomhashtags.worldlaws.recent.software.console.PlayStation4Updates;
import me.randomhashtags.worldlaws.recent.software.console.PlayStation5Updates;
import me.randomhashtags.worldlaws.recent.software.other.AppleSoftwareUpdates;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public enum RecentEvents {
    INSTANCE;

    private final RecentEventController[] events = new RecentEventController[] {
            AppleSoftwareUpdates.INSTANCE,
            PlayStation4Updates.INSTANCE,
            PlayStation5Updates.INSTANCE,
            VideoGameUpdates.INSTANCE
    };

    public void refresh(CompletionHandler handler) {
        final long started = System.currentTimeMillis();
        final LocalDate lastWeek = LocalDate.now().minusDays(7);
        final int max = events.length;
        final ConcurrentHashMap<RecentEventType, HashSet<String>> values = new ConcurrentHashMap<>();
        final AtomicInteger completed = new AtomicInteger(0);
        Arrays.stream(events).parallel().forEach(event -> event.refresh(lastWeek, new CompletionHandler() {
            @Override
            public void handleObject(Object object) {
                if(object != null) {
                    @SuppressWarnings({ "unchecked" })
                    final HashSet<PreRecentEvent> recentEvents = (HashSet<PreRecentEvent>) object;
                    if(!recentEvents.isEmpty()) {
                        final RecentEventType type = event.getType();
                        values.putIfAbsent(type, new HashSet<>());

                        final HashMap<String, HashSet<String>> hashmap = new HashMap<>();
                        for(PreRecentEvent recentEvent : recentEvents) {
                            final String dateString = recentEvent.getDate().getDateString();
                            hashmap.putIfAbsent(dateString, new HashSet<>());
                            hashmap.get(dateString).add(recentEvent.toString());
                        }
                        for(Map.Entry<String, HashSet<String>> map : hashmap.entrySet()) {
                            final String dateString = map.getKey();
                            final StringBuilder builder = new StringBuilder("\"" + dateString + "\":{");
                            final HashSet<String> set = map.getValue();
                            boolean isFirst = true;
                            for(String string : set) {
                                builder.append(isFirst ? "" : ",").append(string);
                                isFirst = false;
                            }
                            builder.append("}");
                            values.get(type).add(builder.toString());
                        }
                    }
                }

                if(completed.addAndGet(1) == max) {
                    completeHandler(started, values, handler);
                }
            }

            @Override
            public void handleHashSetString(HashSet<String> hashset) {
                final int amount = hashset != null ? hashset.size() : 0;
                if(amount > 0) {
                    final RecentEventType type = event.getType();
                    values.putIfAbsent(type, new HashSet<>());
                    values.get(type).addAll(hashset);
                }
                if(completed.addAndGet(1) == max) {
                    completeHandler(started, values, handler);
                }
            }
        }));
    }
    private void completeHandler(long started, ConcurrentHashMap<RecentEventType, HashSet<String>> values, CompletionHandler handler) {
        String value = null;
        if(!values.isEmpty()) {
            final StringBuilder builder = new StringBuilder("{");
            boolean isFirstType = true;
            for(Map.Entry<RecentEventType, HashSet<String>> map : values.entrySet()) {
                final RecentEventType type = map.getKey();
                final HashSet<String> set = map.getValue();
                builder.append(isFirstType ? "" : ",").append("\"").append(type.name().toLowerCase()).append("\":{");
                isFirstType = false;
                boolean isFirst = true;
                for(String s : set) {
                    builder.append(isFirst ? "" : ",").append(s);
                    isFirst = false;
                }
                builder.append("}");
            }
            builder.append("}");
            value = builder.toString();
        }
        WLLogger.logInfo("RecentEvents - loaded (took " + (System.currentTimeMillis()-started) + "ms)");
        handler.handleString(value);
    }
}
