package me.randomhashtags.worldlaws.upcoming;

import me.randomhashtags.worldlaws.CompletionHandler;
import me.randomhashtags.worldlaws.WLLogger;
import org.apache.logging.log4j.Level;

public abstract class LoadedUpcomingEventController extends UpcomingEventController {
    @Override
    public void loadUpcomingEvent(String id, CompletionHandler handler) {
        WLLogger.log(Level.WARN, getClass().getSimpleName() + " (" + getType().name() + ") - tried loading event with id \"" + id + "\"! This shouldn't happen!");
        handler.handleString(null);
    }
}
