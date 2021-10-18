package me.randomhashtags.worldlaws.upcoming.sports;

import me.randomhashtags.worldlaws.CompletionHandler;
import me.randomhashtags.worldlaws.WLLogger;
import me.randomhashtags.worldlaws.upcoming.USAUpcomingEventController;
import me.randomhashtags.worldlaws.upcoming.UpcomingEventType;
import org.apache.logging.log4j.Level;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

public final class NFL extends USAUpcomingEventController {

    @Override
    public UpcomingEventType getType() {
        return null;
    }

    @Override
    public void load(CompletionHandler handler) {
        final String url = "https://www.nfl.com/schedules/";
        final Document doc = getDocument(url);
        if(doc != null) {
            WLLogger.log(Level.INFO, "doc=" + doc.toString());
            final Elements dates = doc.select("div.d3-l-wrap main div section.d3-l-grid-outer");
            handler.handleString(null);
        } else {
            handler.handleString(null);
        }
    }

    @Override
    public void loadUpcomingEvent(String id, CompletionHandler handler) {
        handler.handleString(null);
    }
}
