package me.randomhashtags.worldlaws.upcoming.sports;

import me.randomhashtags.worldlaws.WLLogger;
import me.randomhashtags.worldlaws.upcoming.USAUpcomingEventController;
import me.randomhashtags.worldlaws.upcoming.UpcomingEventType;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

public final class NFL extends USAUpcomingEventController {

    @Override
    public UpcomingEventType getType() {
        return UpcomingEventType.SPORT_NFL;
    }

    @Override
    public void load() {
        final String url = "https://www.nfl.com/schedules/";
        final Document doc = getDocument(url);
        if(doc != null) {
            final Elements dates = doc.select("div.d3-l-col__col-12");
            WLLogger.logInfo("NFL;dates=" + dates.toString());
        }
    }

    @Override
    public String loadUpcomingEvent(String id) {
        return null;
    }
}
