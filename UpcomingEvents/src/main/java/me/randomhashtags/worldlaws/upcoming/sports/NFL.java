package me.randomhashtags.worldlaws.upcoming.sports;

import me.randomhashtags.worldlaws.CompletionHandler;
import me.randomhashtags.worldlaws.PreUpcomingEvent;
import me.randomhashtags.worldlaws.WLLogger;
import me.randomhashtags.worldlaws.upcoming.USAUpcomingEventController;
import me.randomhashtags.worldlaws.upcoming.UpcomingEventType;
import org.apache.logging.log4j.Level;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.HashMap;

public enum NFL implements USAUpcomingEventController {
    INSTANCE;

    private HashMap<String, PreUpcomingEvent> preEventURLS;
    private HashMap<String, String> upcomingEvents, preUpcomingEvents;

    @Override
    public UpcomingEventType getType() {
        return null;
    }

    @Override
    public HashMap<String, PreUpcomingEvent> getPreUpcomingEvents() {
        return preEventURLS;
    }

    @Override
    public HashMap<String, String> getLoadedPreUpcomingEvents() {
        return preUpcomingEvents;
    }

    @Override
    public HashMap<String, String> getUpcomingEvents() {
        return upcomingEvents;
    }

    @Override
    public void load(CompletionHandler handler) {
        preEventURLS = new HashMap<>();
        upcomingEvents = new HashMap<>();
        preUpcomingEvents = new HashMap<>();
        final String url = "https://www.nfl.com/schedules/";
        final Document doc = getDocument(url);
        if(doc != null) {
            WLLogger.log(Level.INFO, "doc=" + doc.toString());
            final Elements dates = doc.select("div.d3-l-wrap main div section.d3-l-grid-outer");
            handler.handleString(null);
        }
    }

    @Override
    public void loadUpcomingEvent(String id, CompletionHandler handler) {
    }
}
