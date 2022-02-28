package me.randomhashtags.worldlaws;

import me.randomhashtags.worldlaws.notifications.RemoteNotification;
import me.randomhashtags.worldlaws.notifications.RemoteNotificationCategory;
import me.randomhashtags.worldlaws.recent.PreRecentEvent;
import me.randomhashtags.worldlaws.recent.RecentEventController;
import me.randomhashtags.worldlaws.recent.RecentEventType;
import me.randomhashtags.worldlaws.recent.VideoGameUpdates;
import me.randomhashtags.worldlaws.recent.software.console.PlayStation4Updates;
import me.randomhashtags.worldlaws.recent.software.console.PlayStation5Updates;
import me.randomhashtags.worldlaws.recent.software.other.AppleSoftwareUpdates;
import me.randomhashtags.worldlaws.stream.CompletableFutures;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public enum RecentEvents {
    INSTANCE;

    private final RecentEventController[] events = new RecentEventController[] {
            new AppleSoftwareUpdates(),
            new PlayStation4Updates(),
            new PlayStation5Updates(),
            new VideoGameUpdates()
    };

    public String refresh(int daysOffset) {
        final long started = System.currentTimeMillis();
        final LocalDate lastWeek = LocalDate.now().minusDays(daysOffset);
        final ConcurrentHashMap<RecentEventType, ConcurrentHashMap<String, HashSet<String>>> allValues = new ConcurrentHashMap<>();
        new CompletableFutures<RecentEventController>().stream(Arrays.asList(events), controller -> {
            final HashSet<PreRecentEvent> preRecentEvents = controller.refreshHashSet(lastWeek);
            final HashSet<PreRecentEvent> newEvents = controller.getNewInformation(preRecentEvents);
            if(newEvents != null) {
                new CompletableFutures<PreRecentEvent>().stream(preRecentEvents, event -> {
                    final RemoteNotificationCategory category = event.getRemoteNotificationCategory();
                    new RemoteNotification(category, false, category.getTitle(), event.getTitle(), event.getDescription());
                });
                RemoteNotification.pushPending();
            }
            if(preRecentEvents != null && !preRecentEvents.isEmpty()) {
                final RecentEventType type = controller.getType();
                allValues.putIfAbsent(type, new ConcurrentHashMap<>());
                for(PreRecentEvent recentEvent : preRecentEvents) {
                    final String dateString = recentEvent.getDate().getDateString();
                    allValues.get(type).putIfAbsent(dateString, new HashSet<>());
                    allValues.get(type).get(dateString).add(recentEvent.toString());
                }
            }
            final ConcurrentHashMap<String, HashSet<String>> hashmap = controller.refreshHashMap(lastWeek);
            if(hashmap != null && !hashmap.isEmpty()) {
                final RecentEventType type = controller.getType();
                allValues.putIfAbsent(type, new ConcurrentHashMap<>());
                allValues.get(type).putAll(hashmap);
            }
        });
        return completeHandler(started, allValues);
    }
    private String completeHandler(long started, ConcurrentHashMap<RecentEventType, ConcurrentHashMap<String, HashSet<String>>> values) {
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
        WLLogger.logInfo("RecentEvents - loaded (took " + WLUtilities.getElapsedTime (started) + ")");
        return value;
    }
}
