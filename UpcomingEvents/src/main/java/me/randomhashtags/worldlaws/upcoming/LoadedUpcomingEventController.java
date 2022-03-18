package me.randomhashtags.worldlaws.upcoming;

import me.randomhashtags.worldlaws.WLLogger;
import me.randomhashtags.worldlaws.upcoming.events.UpcomingEvent;

public abstract class LoadedUpcomingEventController extends UpcomingEventController {
    @Override
    public UpcomingEvent loadUpcomingEvent(String id) {
        WLLogger.logError(this, "type=" + getType().name() + " - tried loading event with id \"" + id + "\", which is illegal!");
        return null;
    }
}
