package me.randomhashtags.worldlaws.recode.event;

import me.randomhashtags.worldlaws.*;
import me.randomhashtags.worldlaws.event.EventDate;
import me.randomhashtags.worldlaws.event.sports.SportEvent;
import me.randomhashtags.worldlaws.recode.NewUSAEventController;
import org.apache.logging.log4j.Level;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.time.Month;
import java.util.HashMap;
import java.util.Optional;

public enum NewUFC implements NewUSAEventController {
    INSTANCE;

    private HashMap<String, PreUpcomingEvent> preEvents;
    private HashMap<String, String> preEventURLs, events;

    @Override
    public UpcomingEventType getType() {
        return UpcomingEventType.SPORT_UFC;
    }

    @Override
    public HashMap<String, PreUpcomingEvent> getPreEvents() {
        return preEvents;
    }

    @Override
    public EventSource getSource() {
        return new EventSource("Wikipedia: List of UFC events", "https://en.wikipedia.org/wiki/List_of_UFC_events");
    }

    @Override
    public void refresh(CompletionHandler handler) {
        preEvents = new HashMap<>();
        preEventURLs = new HashMap<>();
        events = new HashMap<>();
        final long started = System.currentTimeMillis();
        final String wikipagePrefix = "https://en.wikipedia.org";
        final Document doc = getDocument(wikipagePrefix + "/wiki/List_of_UFC_events");
        if(doc != null) {
            final Elements tables = doc.select("table.sortable");
            final Optional<Element> scheduledEventsTable = tables.stream().filter(tableElement -> tableElement.attr("id").equals("Scheduled_events")).findFirst();
            if(scheduledEventsTable.isPresent()) {
                final Elements elements = scheduledEventsTable.get().select("tbody tr");
                elements.remove(0);
                String lastVenue = null;
                for(Element element : elements) {
                    final Elements rows = element.select("td");
                    final int rowSize = rows.size();
                    final Element eventElement = rows.get(0);
                    final String event = eventElement.text(), dateString = rows.get(1).text();
                    final String venue = rowSize > 3 ? rows.get(2).text() : lastVenue;
                    final String location = rows.get(rowSize-1).text();
                    lastVenue = venue;
                    final String[] dateValues = dateString.split(", "), dates = dateValues[0].split(" ");
                    final Month month = WLUtilities.getMonthFromPrefix(dates[0]);
                    final int day = Integer.parseInt(dates[1]), year = Integer.parseInt(dateValues[1]);
                    final EventDate date = new EventDate(month, day, year);
                    final Elements hrefs = eventElement.select("a");
                    if(!hrefs.isEmpty()) {
                        final String wikipageURL = wikipagePrefix + hrefs.get(0).attr("href");
                        final String id = getEventIdentifier(date, event);

                        //final PreUpcomingEvent preUpcomingEvent = new PreUpcomingEvent(date, event, location, posterURL);
                        //preEvents.put(id, preUpcomingEvent);
                        //preEventURLs.put(id, wikipageURL);
                    }
                }
            }
            WLLogger.log(Level.INFO, "UFC - refreshed upcoming events (took " + (System.currentTimeMillis()-started) + "ms)");
            handler.handle(null);
        }
    }

    @Override
    public void getEvent(String id, CompletionHandler handler) {
        if(events.containsKey(id)) {
            handler.handle(events.get(id));
        } else if(preEventURLs.containsKey(id)) {
            final PreUpcomingEvent preUpcomingEvent = preEvents.get(id);
            final String url = preEventURLs.get(id);
            final Document wikidoc = getDocument(url);
            if(wikidoc != null) {
                final UpcomingEventType type = getType();
                final EventSource source = new EventSource("Wikipedia: ???", url);
                final EventSources sources = new EventSources(source, getSource());

                final EventDate date = new EventDate(null, -1, -1);// preUpcomingEvent.getDate();
                final String description = wikidoc.select("div.mw-parser-output p").get(0).text();
                final Elements infobox = wikidoc.select("table.infobox tbody tr");
                final String title = preUpcomingEvent.getTitle();
                final Elements image = infobox.get(1).select("td a img");
                final boolean hasImage = !image.isEmpty();
                final String posterURL = hasImage ? "https:" + image.attr("src") : null;
                final String venue = infobox.get(hasImage ? 5 : 4).select("td").get(0).text();
                final String city = infobox.get(hasImage ? 6 : 5).select("td").get(0).text();

                final SportEvent ufc = new SportEvent(type, date, title, description, city, posterURL, venue, sources);
                final String string = ufc.toJSON();
                events.put(id, string);
                preEventURLs.remove(id);
                handler.handle(string);
            }
        }
    }
}
