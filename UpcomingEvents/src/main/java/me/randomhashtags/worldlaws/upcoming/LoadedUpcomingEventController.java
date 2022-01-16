package me.randomhashtags.worldlaws.upcoming;

import me.randomhashtags.worldlaws.WLLogger;

public abstract class LoadedUpcomingEventController extends UpcomingEventController {
    @Override
    public String loadUpcomingEvent(String id) {
        WLLogger.logError(this, "type=" + getType().name() + " - tried loading event with id \"" + id + "\", which is illegal!");
        return null;
    }
}
