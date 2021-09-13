package me.randomhashtags.worldlaws.upcoming;

import me.randomhashtags.worldlaws.CompletionHandler;
import me.randomhashtags.worldlaws.PreUpcomingEvent;
import me.randomhashtags.worldlaws.WLLogger;
import org.apache.logging.log4j.Level;

import java.util.HashMap;

public interface LoadedUpcomingEventController extends UpcomingEventController {

    @Override
    default HashMap<String, PreUpcomingEvent> getPreUpcomingEvents() {
        return null;
    }

    @Override
    default void loadUpcomingEvent(String id, CompletionHandler handler) {
        WLLogger.log(Level.WARN, getClass().getSimpleName() + " (" + getType().name() + ") - tried loading event with id \"" + id + "\"! This shouldn't happen!");
        handler.handleString(null);
    }
}
