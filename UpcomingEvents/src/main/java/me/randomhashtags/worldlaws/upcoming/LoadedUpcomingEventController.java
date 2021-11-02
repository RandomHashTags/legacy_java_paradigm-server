package me.randomhashtags.worldlaws.upcoming;

import me.randomhashtags.worldlaws.CompletionHandler;
import me.randomhashtags.worldlaws.WLLogger;

public abstract class LoadedUpcomingEventController extends UpcomingEventController {
    @Override
    public void loadUpcomingEvent(String id, CompletionHandler handler) {
        WLLogger.logError(this, "type=" + getType().name() + " - tried loading event with id \"" + id + "\", which is illegal!");
        handler.handleString(null);
    }
}
