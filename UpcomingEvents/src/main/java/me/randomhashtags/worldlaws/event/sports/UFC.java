package me.randomhashtags.worldlaws.event.sports;

import me.randomhashtags.worldlaws.*;
import me.randomhashtags.worldlaws.event.USAEventController;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.time.Month;
import java.util.HashMap;

public enum UFC implements USAEventController {
    INSTANCE;

    private HashMap<String, NewPreUpcomingEvent> preEventURLS;
    private HashMap<String, String> upcomingEvents, preUpcomingEvents;

    @Override
    public UpcomingEventType getType() {
        return UpcomingEventType.SPORT_UFC;
    }

    @Override
    public HashMap<String, NewPreUpcomingEvent> getPreEventURLs() {
        return preEventURLS;
    }

    @Override
    public HashMap<String, String> getPreUpcomingEvents() {
        return preUpcomingEvents;
    }

    @Override
    public HashMap<String, String> getUpcomingEvents() {
        return upcomingEvents;
    }

    @Override
    public void load(CompletionHandler handler) {
        preEventURLS = new HashMap<>();
        preUpcomingEvents = new HashMap<>();
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
                    for(Element element : elements) {
                        final Elements rows = element.select("td");
                        final int rowSize = rows.size();
                        final Element eventElement = rows.get(0);
                        final String event = eventElement.text(), dateString = rows.get(1).text(), location = rows.get(rowSize-2).text();
                        final String[] dateValues = dateString.split(", "), dates = dateValues[0].split(" ");
                        final Month month = WLUtilities.getMonthFromPrefix(dates[0]);
                        if(month != null) {
                            final int day = Integer.parseInt(dates[1]), year = Integer.parseInt(dateValues[1]);
                            final Elements hrefs = eventElement.select("a");
                            if(!hrefs.isEmpty()) {
                                final String wikipageURL = wikipagePrefix + hrefs.get(0).attr("href");
                                final String id = month.getValue() + "-" + year + "-" + day + "." + event.replace(" ", "");
                                final NewPreUpcomingEvent preUpcomingEvent = new NewPreUpcomingEvent(id, event, wikipageURL, location);
                                preEventURLS.put(id, preUpcomingEvent);
                            }
                        }
                    }
                    break;
                }
            }
            handler.handle(null);
        }
    }

    @Override
    public void getUpcomingEvent(String id, CompletionHandler handler) {
        if(upcomingEvents.containsKey(id)) {
            handler.handle(upcomingEvents.get(id));
        } else {
            final NewPreUpcomingEvent preUpcomingEvent = preEventURLS.get(id);
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
                final String preUpcomingEventString = preUpcomingEvent.getPreUpcomingEvent(posterURL).toString();
                preUpcomingEvents.put(id, preUpcomingEventString);
                handler.handle(string);
            } else {
                handler.handle(null);
            }
        }
    }
}
