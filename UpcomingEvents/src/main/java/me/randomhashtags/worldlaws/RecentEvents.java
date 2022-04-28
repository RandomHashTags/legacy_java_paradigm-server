package me.randomhashtags.worldlaws;

import me.randomhashtags.worldlaws.locale.JSONObjectTranslatable;
import me.randomhashtags.worldlaws.notifications.RemoteNotification;
import me.randomhashtags.worldlaws.notifications.RemoteNotificationSubcategory;
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

    public JSONObjectTranslatable refresh(int daysOffset) {
        final long started = System.currentTimeMillis();
        final LocalDate lastWeek = LocalDate.now().minusDays(daysOffset);
        final ConcurrentHashMap<RecentEventType, ConcurrentHashMap<String, HashSet<PreRecentEvent>>> allValues = new ConcurrentHashMap<>();
        final HashSet<PreRecentEvent> newEvents = new HashSet<>();
        new CompletableFutures<RecentEventController>().stream(Arrays.asList(events), controller -> {
            final HashSet<PreRecentEvent> preRecentEvents = controller.refreshHashSet(lastWeek);
            final HashSet<PreRecentEvent> newInformation = controller.getNewInformation(preRecentEvents);
            if(newInformation != null) {
                newEvents.addAll(newInformation);
            }
            if(preRecentEvents != null && !preRecentEvents.isEmpty()) {
                final RecentEventType type = controller.getType();
                allValues.putIfAbsent(type, new ConcurrentHashMap<>());
                for(PreRecentEvent recentEvent : preRecentEvents) {
                    final String dateString = recentEvent.getDate().getDateString();
                    allValues.get(type).putIfAbsent(dateString, new HashSet<>());
                    allValues.get(type).get(dateString).add(recentEvent);
                }
            }
            final ConcurrentHashMap<String, HashSet<PreRecentEvent>> hashmap = controller.refreshHashMap(lastWeek);
            if(hashmap != null && !hashmap.isEmpty()) {
                final RecentEventType type = controller.getType();
                allValues.putIfAbsent(type, new ConcurrentHashMap<>());
                allValues.get(type).putAll(hashmap);
            }
        });
        if(!newEvents.isEmpty()) {
            new CompletableFutures<PreRecentEvent>().stream(newEvents, event -> {
                final RemoteNotificationSubcategory category = event.getRemoteNotificationCategory();
                new RemoteNotification(category, false, category.getName(), event.getTitle(), event.getDescription());
            });
            RemoteNotification.pushPending();
        }
        return completeHandler(started, allValues);
    }
    private JSONObjectTranslatable completeHandler(long started, ConcurrentHashMap<RecentEventType, ConcurrentHashMap<String, HashSet<PreRecentEvent>>> values) {
        JSONObjectTranslatable json = null;
        if(!values.isEmpty()) {
            json = new JSONObjectTranslatable();
            for(Map.Entry<RecentEventType, ConcurrentHashMap<String, HashSet<PreRecentEvent>>> map : values.entrySet()) {
                final RecentEventType type = map.getKey();
                final String typeID = type.name().toLowerCase();
                final JSONObjectTranslatable typeJSON = new JSONObjectTranslatable();
                for(Map.Entry<String, HashSet<PreRecentEvent>> dates : map.getValue().entrySet()) {
                    final String dateString = dates.getKey();
                    final JSONObjectTranslatable valuesJSON = new JSONObjectTranslatable();
                    for(PreRecentEvent event : dates.getValue()) {
                        final String identifier = event.getIdentifier();
                        valuesJSON.put(identifier, event.toJSONObject());
                        valuesJSON.addTranslatedKey(identifier);
                    }
                    typeJSON.put(dateString, valuesJSON);
                    typeJSON.addTranslatedKey(dateString);
                }
                json.put(typeID, typeJSON);
                json.addTranslatedKey(typeID);
            }
        }
        WLLogger.logInfo("RecentEvents - loaded (took " + WLUtilities.getElapsedTime (started) + ")");
        return json;
    }
}
