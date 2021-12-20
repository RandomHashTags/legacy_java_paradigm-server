package me.randomhashtags.worldlaws;

import me.randomhashtags.worldlaws.recent.PreRecentEvent;
import me.randomhashtags.worldlaws.recent.RecentEventController;
import me.randomhashtags.worldlaws.recent.RecentEventType;
import me.randomhashtags.worldlaws.recent.VideoGameUpdates;
import me.randomhashtags.worldlaws.recent.software.console.PlayStation4Updates;
import me.randomhashtags.worldlaws.recent.software.console.PlayStation5Updates;
import me.randomhashtags.worldlaws.recent.software.other.AppleSoftwareUpdates;
import me.randomhashtags.worldlaws.stream.ParallelStream;

import java.time.LocalDate;
import java.util.Arrays;
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
        final ConcurrentHashMap<RecentEventType, ConcurrentHashMap<String, HashSet<String>>> allValues = new ConcurrentHashMap<>();
        final AtomicInteger completed = new AtomicInteger(0);
        ParallelStream.stream(Arrays.asList(events), eventObj -> {
            final RecentEventController event = (RecentEventController) eventObj;
            event.refresh(lastWeek, new CompletionHandler() {
                @Override
                public void handleObject(Object object) {
                    if(object != null) {
                        @SuppressWarnings({ "unchecked" })
                        final HashSet<PreRecentEvent> recentEvents = (HashSet<PreRecentEvent>) object;
                        if(!recentEvents.isEmpty()) {
                            final RecentEventType type = event.getType();
                            allValues.putIfAbsent(type, new ConcurrentHashMap<>());
                            for(PreRecentEvent recentEvent : recentEvents) {
                                final String dateString = recentEvent.getDate().getDateString();
                                allValues.get(type).putIfAbsent(dateString, new HashSet<>());
                                allValues.get(type).get(dateString).add(recentEvent.toString());
                            }
                        }
                    }

                    if(completed.addAndGet(1) == max) {
                        completeHandler(started, allValues, handler);
                    }
                }

                @Override
                public void handleConcurrentHashMapHashSetString(ConcurrentHashMap<String, HashSet<String>> hashmap) {
                    final int amount = hashmap != null ? hashmap.size() : 0;
                    if(amount > 0) {
                        final RecentEventType type = event.getType();
                        allValues.putIfAbsent(type, new ConcurrentHashMap<>());
                        allValues.get(type).putAll(hashmap);
                    }
                    if(completed.addAndGet(1) == max) {
                        completeHandler(started, allValues, handler);
                    }
                }
            });
        });
    }
    private void completeHandler(long started, ConcurrentHashMap<RecentEventType, ConcurrentHashMap<String, HashSet<String>>> values, CompletionHandler handler) {
        String value = null;
        if(!values.isEmpty()) {
            final StringBuilder builder = new StringBuilder("{");
            boolean isFirstType = true;
            for(Map.Entry<RecentEventType, ConcurrentHashMap<String, HashSet<String>>> map : values.entrySet()) {
                final RecentEventType type = map.getKey();
                builder.append(isFirstType ? "" : ",").append("\"").append(type.name().toLowerCase()).append("\":{");
                isFirstType = false;

                boolean isFirstDateString = true;
                final ConcurrentHashMap<String, HashSet<String>> dateMap = map.getValue();
                for(Map.Entry<String, HashSet<String>> dates : dateMap.entrySet()) {
                    final String dateString = dates.getKey();
                    builder.append(isFirstDateString ? "" : ",").append("\"").append(dateString).append("\":{");
                    isFirstDateString = false;
                    final HashSet<String> set = dates.getValue();
                    boolean isFirst = true;
                    for(String s : set) {
                        builder.append(isFirst ? "" : ",").append(s);
                        isFirst = false;
                    }
                    builder.append("}");
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
