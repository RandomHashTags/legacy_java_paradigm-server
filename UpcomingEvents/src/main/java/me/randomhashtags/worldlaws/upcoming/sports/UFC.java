package me.randomhashtags.worldlaws.upcoming.sports;

import me.randomhashtags.worldlaws.*;
import me.randomhashtags.worldlaws.upcoming.USAUpcomingEventController;
import me.randomhashtags.worldlaws.upcoming.UpcomingEventType;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.time.Month;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

public enum UFC implements USAUpcomingEventController {
    INSTANCE;

    private HashMap<String, PreUpcomingEvent> preUpcomingEvents;
    private HashMap<String, String> upcomingEvents, loadedPreUpcomingEvents;

    @Override
    public UpcomingEventType getType() {
        return UpcomingEventType.SPORT_UFC;
    }

    @Override
    public HashMap<String, PreUpcomingEvent> getPreUpcomingEvents() {
        return preUpcomingEvents;
    }

    @Override
    public HashMap<String, String> getLoadedPreUpcomingEvents() {
        return loadedPreUpcomingEvents;
    }

    @Override
    public HashMap<String, String> getUpcomingEvents() {
        return upcomingEvents;
    }

    @Override
    public void load(CompletionHandler handler) {
        preUpcomingEvents = new HashMap<>();
        loadedPreUpcomingEvents = new HashMap<>();
        upcomingEvents = new HashMap<>();

        final String wikipagePrefix = "https://en.wikipedia.org";
        final String url = wikipagePrefix + "/wiki/List_of_UFC_events";
        final Document doc = getDocument(url);
        if(doc != null) {
            final Elements table = doc.select("table.sortable");
            for(Element tableElement : table) {
                if(tableElement.attr("id").equals("Scheduled_events")) {
                    final Elements elements = tableElement.select("tbody tr");
                    elements.remove(0);
                    final AtomicInteger completed = new AtomicInteger(0);
                    final int max = elements.size();
                    elements.parallelStream().forEach(element -> {
                        final Elements rows = element.select("td");
                        final int rowSize = rows.size();
                        final Element eventElement = rows.get(0);
                        final String event = eventElement.text(), dateElementString = rows.get(1).text(), location = rows.get(rowSize-2).text();
                        final String[] dateValues = dateElementString.split(", "), dates = dateValues[0].split(" ");
                        final Month month = WLUtilities.valueOfMonthFromInput(dates[0]);
                        if(month != null) {
                            final int day = Integer.parseInt(dates[1]), year = Integer.parseInt(dateValues[1]);
                            final Elements hrefs = eventElement.select("a");
                            if(!hrefs.isEmpty()) {
                                final String wikipageURL = wikipagePrefix + hrefs.get(0).attr("href");
                                final String dateString = getEventDateString(year, month, day), id = getEventDateIdentifier(dateString, event);
                                final PreUpcomingEvent preUpcomingEvent = new PreUpcomingEvent(id, event, wikipageURL, location);
                                preUpcomingEvents.put(id, preUpcomingEvent);
                            }
                        }
                        if(completed.addAndGet(1) == max) {
                            handler.handleString(null);
                        }
                    });
                    return;
                }
            }
        } else {
            handler.handleString(null);
        }
    }

    @Override
    public void loadUpcomingEvent(String id, CompletionHandler handler) {
        final PreUpcomingEvent preUpcomingEvent = preUpcomingEvents.get(id);
        final String url = preUpcomingEvent.getURL();
        final String title = preUpcomingEvent.getTitle(), location = preUpcomingEvent.getTag();
        final Document wikidoc = getDocument(url);
        if(wikidoc != null) {
            final String description = removeReferences(wikidoc.select("div.mw-parser-output p").get(0).text());
            final Elements infobox = wikidoc.select("table.infobox tbody tr");
            final Elements image = infobox.get(1).select("td a img");
            final String posterURL = !image.isEmpty() ? "https:" + image.attr("src") : null;
            final EventSource source = new EventSource("Wikipedia: " + title, url);

            final EventSource listOfEventsSource = new EventSource("Wikipedia: List of UFC events", "https://en.wikipedia.org/wiki/List_of_UFC_events");
            final EventSources sources = new EventSources(listOfEventsSource, source);
            final SportEvent ufc = new SportEvent(title, description, location, posterURL, "unknown venue", sources);
            final String string = ufc.toJSON();
            upcomingEvents.put(id, string);
            handler.handleString(string);
        } else {
            handler.handleString(null);
        }
    }
}
