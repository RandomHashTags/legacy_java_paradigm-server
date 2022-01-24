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
    public ConcurrentHashMap<String, HashSet<String>> refreshHashMap(LocalDate startingDate) {
        return null;
    }

    public boolean hasNewInformation(HashSet<PreRecentEvent> events) {
        boolean value = false;
        if(lastUpdates != null && events != null) {
            for(PreRecentEvent newEvent : events) {
                boolean has = false;
                for(PreRecentEvent event : lastUpdates) {
                    if(newEvent.areEqual(event)) {
                        has = true;
                        break;
                    }
                }
                if(!has) {
                    value = true;
                    break;
                }
            }
        }
        lastUpdates = events;
        return value;
    }
}
