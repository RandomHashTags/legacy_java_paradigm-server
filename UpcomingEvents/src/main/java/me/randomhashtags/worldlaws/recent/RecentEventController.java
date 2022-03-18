package me.randomhashtags.worldlaws.recent;

import me.randomhashtags.worldlaws.Jsoupable;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.concurrent.ConcurrentHashMap;

public abstract class RecentEventController implements Jsoupable {

    protected HashSet<PreRecentEvent> lastUpdates;

    public abstract RecentEventType getType();
    public HashSet<PreRecentEvent> refreshHashSet(LocalDate startingDate) {
        return null;
    }
    public ConcurrentHashMap<String, HashSet<PreRecentEvent>> refreshHashMap(LocalDate startingDate) {
        return null;
    }

    public HashSet<PreRecentEvent> getNewInformation(HashSet<PreRecentEvent> events) {
        HashSet<PreRecentEvent> newEvents = null;
        if(lastUpdates != null && events != null) {
            newEvents = new HashSet<>();
            for(PreRecentEvent newEvent : events) {
                boolean has = false;
                for(PreRecentEvent event : lastUpdates) {
                    if(newEvent.areEqual(event)) {
                        has = true;
                        break;
                    }
                }
                if(!has) {
                    newEvents.add(newEvent);
                }
            }
        }
        lastUpdates = events;
        return newEvents != null && !newEvents.isEmpty() ? newEvents : null;
    }
}
