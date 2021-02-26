package me.randomhashtags.worldlaws.event.sports;

import me.randomhashtags.worldlaws.CompletionHandler;
import me.randomhashtags.worldlaws.WLLogger;
import me.randomhashtags.worldlaws.event.USAEventController;
import me.randomhashtags.worldlaws.UpcomingEventType;
import org.apache.logging.log4j.Level;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.HashMap;

public enum NFL implements USAEventController {
    INSTANCE;

    private String json;

    @Override
    public UpcomingEventType getType() {
        return UpcomingEventType.SPORT_NFL;
    }

    @Override
    public void getUpcomingEvents(CompletionHandler handler) {
        if(json != null) {
            handler.handle(json);
        } else {
            refreshSchedule(handler);
        }
    }

    @Override
    public HashMap<String, String> getPreEvents() {
        return null;
    }

    @Override
    public HashMap<String, String> getEvents() {
        return null;
    }

    private void refreshSchedule(CompletionHandler handler) {
        final long started = System.currentTimeMillis();
        final String url = "https://www.nfl.com/schedules/";
        final Document doc = getDocument(url);
        if(doc != null) {
            WLLogger.log(Level.INFO, "doc=" + doc.toString());
            final Elements dates = doc.select("div.d3-l-wrap main div section.d3-l-grid-outer");
            json = "[]";
            WLLogger.log(Level.INFO, "NFL - refreshed schedule (took " + (System.currentTimeMillis()-started) + "ms)");
            handler.handle(json);
        }
    }
}
