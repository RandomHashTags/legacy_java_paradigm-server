package me.randomhashtags.worldlaws.upcoming.sports;

import me.randomhashtags.worldlaws.*;
import me.randomhashtags.worldlaws.upcoming.USAUpcomingEventController;
import me.randomhashtags.worldlaws.upcoming.UpcomingEventType;
import me.randomhashtags.worldlaws.upcoming.events.SportEvent;
import me.randomhashtags.worldlaws.upcoming.events.UpcomingEvent;
import org.json.JSONObject;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.time.LocalDate;
import java.time.Month;

public final class UFC extends USAUpcomingEventController {

    @Override
    public UpcomingEventType getType() {
        return UpcomingEventType.SPORT_UFC;
    }

    @Override
    public void load() {
        final LocalDate today = LocalDate.now();
        final String url = "https://en.wikipedia.org/wiki/List_of_UFC_events";
        final Document doc = getDocument(url);
        if(doc != null) {
            final Elements tables = doc.select("table.sortable");
            for(Element table : tables) {
                final String tableID = table.attr("id");
                final int offset = tableID.equals("Scheduled_events") ? 0 : tableID.equals("Past_events") ? 1 : -1;
                if(offset >= 0) {
                    load(today, table, offset);
                }
            }
        }
    }
    private void load(LocalDate today, Element table, int offset) {
        final String wikipagePrefix = "https://en.wikipedia.org";
        final Elements elements = table.select("tbody tr");
        elements.remove(0);
        if(elements.size() > 0) {
            for(Element element : elements) {
                final Elements rows = element.select("td");
                final Element eventElement = rows.get(offset);
                final String dateElementString = rows.get(offset + 1).text();
                final String[] dateValues = dateElementString.split(", "), dates = dateValues[0].split(" ");
                final Month month = WLUtilities.valueOfMonthFromInput(dates[0]);
                if(month != null) {
                    final int day = Integer.parseInt(dates[1]), year = Integer.parseInt(dateValues[1]);
                    final LocalDate date = LocalDate.of(year, month, day);
                    if(date.isAfter(today) || date.isEqual(today)) {
                        final Elements hrefs = eventElement.select("a");
                        if(!hrefs.isEmpty()) {
                            final String event = eventElement.text();
                            final String location = rows.get(rows.size() - 2 - offset).text();
                            final String wikipageURL = wikipagePrefix + hrefs.get(0).attr("href");
                            final String dateString = EventDate.getDateString(year, day, month), id = getEventDateIdentifier(dateString, event);
                            final PreUpcomingEvent preUpcomingEvent = new PreUpcomingEvent(id, event, wikipageURL, location);
                            putPreUpcomingEvent(id, preUpcomingEvent);
                        }
                    }
                }
            }
        }
    }

    @Override
    public UpcomingEvent loadUpcomingEvent(String id) {
        final PreUpcomingEvent preUpcomingEvent = getPreUpcomingEvent(id);
        final String url = preUpcomingEvent.getURL();
        final String title = preUpcomingEvent.getTitle(), location = preUpcomingEvent.getTag();
        final Document wikidoc = getDocument(url);
        if(wikidoc != null) {
            final String description = LocalServer.removeWikipediaReferences(wikidoc.select("div.mw-parser-output p").get(0).text());
            final Elements infobox = wikidoc.select("table.infobox tbody tr");
            final Elements image = infobox.get(1).select("td a img");
            final String posterURL = !image.isEmpty() ? "https:" + image.attr("src") : null;
            final EventSource source = new EventSource("Wikipedia: " + title, url);

            final EventSources sources = new EventSources(source);
            return new SportEvent(preUpcomingEvent.getEventDate(), title, description, location, posterURL, "unknown venue", sources);
        }
        return null;
    }

    @Override
    public UpcomingEvent parseUpcomingEvent(JSONObject json) {
        return new SportEvent(json);
    }
}
