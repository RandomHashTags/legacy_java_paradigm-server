package me.randomhashtags.worldlaws.event.space;

import me.randomhashtags.worldlaws.CompletionHandler;
import me.randomhashtags.worldlaws.event.EventDate;
import me.randomhashtags.worldlaws.event.EventSource;
import me.randomhashtags.worldlaws.event.EventSources;
import me.randomhashtags.worldlaws.event.USAEventController;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.time.Month;

public enum NASA implements USAEventController {
    INSTANCE;

    private String json;

    @Override
    public String getIdentifier() {
        return "nasa";
    }

    @Override
    public void getUpcomingEvents(CompletionHandler handler) {
        if(json != null) {
            handler.handle(json);
        } else {
            refreshLaunchesAndLandings(handler);
        }
    }

    private void refreshLaunchesAndLandings(CompletionHandler handler) {
        final String url = "https://www.nasa.gov/launchschedule/";
        final Document doc = getDocument(url);
        if(doc != null) {
            final StringBuilder builder = new StringBuilder("[");
            final Elements events = doc.select("div.launch-schedule div.launch-event");
            boolean isFirst = true;
            final int descLength = "Description: ".length();
            final EventSource source = new EventSource("NASA.gov", "https://www.nasa.gov/launchschedule/");
            for(Element event : events) {
                final Elements info = event.select("div.launch-info");
                final String targetDate = info.select("div.ember-view div.date").get(0).text().split(": ")[1];
                final EventDate date = toEventDate(targetDate);
                final String title = info.select("div.title a[href]").get(0).text();
                final String desc = info.select("div.description").get(0).text().substring(descLength);
                final EventSources sources = new EventSources(source);
                final SpaceEvent spaceEvent = new SpaceEvent(date, title, desc, "Unknown", sources);
                builder.append(isFirst ? "" : ",").append(spaceEvent.toJSON());
                isFirst = false;
            }
            builder.append("]");
            json = builder.toString();
            handler.handle(json);
        }
    }
    private EventDate toEventDate(String input) {
        input = input.toLowerCase().replace(",", "");
        final String[] values = input.split(" ");
        final Month month = Month.valueOf(values[0].toUpperCase());
        final int day = Integer.parseInt(values[1]), year = Integer.parseInt(values[2]);
        return new EventDate(month, day, year);
    }
}
