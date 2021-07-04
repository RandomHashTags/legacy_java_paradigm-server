package me.randomhashtags.worldlaws.upcoming.sports;

import me.randomhashtags.worldlaws.CompletionHandler;
import me.randomhashtags.worldlaws.PreUpcomingEvent;
import me.randomhashtags.worldlaws.WLUtilities;
import me.randomhashtags.worldlaws.upcoming.UpcomingEventController;
import me.randomhashtags.worldlaws.upcoming.UpcomingEventType;
import org.jsoup.nodes.Document;

import java.util.HashMap;

public enum Championships implements UpcomingEventController { // https://en.wikipedia.org/wiki/2021_in_sports
    INSTANCE;

    @Override
    public UpcomingEventType getType() {
        return UpcomingEventType.SPORT_CHAMPIONSHIPS;
    }

    @Override
    public HashMap<String, String> getLoadedPreUpcomingEvents() {
        return null;
    }

    @Override
    public HashMap<String, PreUpcomingEvent> getPreUpcomingEvents() {
        return null;
    }

    @Override
    public HashMap<String, String> getUpcomingEvents() {
        return null;
    }

    @Override
    public void load(CompletionHandler handler) {
        final int year = WLUtilities.getTodayYear();
        final String url = "https://en.wikipedia.org/wiki/" + year + "_in_sports";
        final Document doc = getDocument(url);
        if(doc != null) {

        }
    }

    @Override
    public void loadUpcomingEvent(String id, CompletionHandler handler) {

    }
}
