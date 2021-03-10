package me.randomhashtags.worldlaws.event.sports;

import me.randomhashtags.worldlaws.CompletionHandler;
import me.randomhashtags.worldlaws.EventSource;
import me.randomhashtags.worldlaws.WLLogger;
import me.randomhashtags.worldlaws.WLUtilities;
import me.randomhashtags.worldlaws.event.EventDate;
import me.randomhashtags.worldlaws.EventSources;
import me.randomhashtags.worldlaws.PreUpcomingEvent;
import me.randomhashtags.worldlaws.event.USAEventController;
import me.randomhashtags.worldlaws.UpcomingEventType;
import org.apache.logging.log4j.Level;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.time.Month;
import java.util.HashMap;

public enum UFC implements USAEventController {
    INSTANCE;

    private String json;
    private HashMap<String, String> preEvents, events;

    @Override
    public UpcomingEventType getType() {
        return UpcomingEventType.SPORT_UFC;
    }

    @Override
    public void refresh(CompletionHandler handler) {
        final long started = System.currentTimeMillis();
        preEvents = new HashMap<>();
        events = new HashMap<>();
        final String wikipagePrefix = "https://en.wikipedia.org";
        final String url = wikipagePrefix + "/wiki/List_of_UFC_events";
        final Document doc = getDocument(url);
        if(doc != null) {
            final UpcomingEventType type = getType();
            final Elements table = doc.select("table.sortable");
            final StringBuilder builder = new StringBuilder("[");
            final EventSource listOfEventsSource = new EventSource("Wikipedia: List of UFC events", url);
            for(Element tableElement : table) {
                if(tableElement.attr("id").equals("Scheduled_events")) {
                    final Elements elements = tableElement.select("tbody tr");
                    elements.remove(0);
                    String lastVenue = null;
                    boolean isFirst = true;
                    for(Element element : elements) {
                        final Elements rows = element.select("td");
                        final int rowSize = rows.size();
                        final Element eventElement = rows.get(0);
                        final String event = eventElement.text(), dateString = rows.get(1).text(), venue = rowSize > 3 ? rows.get(2).text() : lastVenue, location = rows.get(rowSize-1).text();
                        lastVenue = venue;
                        final String[] dateValues = dateString.split(", "), dates = dateValues[0].split(" ");
                        final Month month = WLUtilities.getMonthFromPrefix(dates[0]);
                        final int day = Integer.parseInt(dates[1]), year = Integer.parseInt(dateValues[1]);
                        final EventDate date = new EventDate(month, day, year);
                        final Elements hrefs = eventElement.select("a");
                        if(!hrefs.isEmpty()) {
                            final String wikipageURL = wikipagePrefix + hrefs.get(0).attr("href");
                            final Document wikidoc = getDocument(wikipageURL);
                            if(wikidoc != null) {
                                final String description = wikidoc.select("div.mw-parser-output p").get(0).text();
                                final Elements infobox = wikidoc.select("table.infobox tbody tr");
                                final Elements image = infobox.get(1).select("td a img");
                                final String posterURL = !image.isEmpty() ? "https:" + image.attr("src") : null;
                                final EventSource source = new EventSource("Wikipedia: " + event, wikipageURL);
                                final EventSources sources = new EventSources(listOfEventsSource, source);
                                final SportEvent ufc = new SportEvent(type, date, event, description, location, posterURL, venue, sources);
                                final String identifier = getEventIdentifier(date, event);
                                events.put(identifier, ufc.toJSON());

                                final PreUpcomingEvent preUpcomingEvent = new PreUpcomingEvent(event, location, posterURL);
                                final String string = preUpcomingEvent.toString();
                                preEvents.put(identifier, string);
                                builder.append(isFirst ? "" : ",").append(string);
                                isFirst = false;
                            }
                        }
                    }
                    break;
                }
            }
            builder.append("]");
            json = builder.toString();
            WLLogger.log(Level.INFO, "UFC - refreshed upcoming events (took " + (System.currentTimeMillis()-started) + "ms)");
            handler.handle(json);
        }
    }

    @Override
    public String getCache() {
        return json;
    }

    @Override
    public HashMap<String, String> getPreEvents() {
        return preEvents;
    }

    @Override
    public HashMap<String, String> getEvents() {
        return events;
    }
}
