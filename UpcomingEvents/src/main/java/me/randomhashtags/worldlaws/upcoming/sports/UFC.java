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

import java.time.Month;

public final class UFC extends USAUpcomingEventController {

    @Override
    public UpcomingEventType getType() {
        return UpcomingEventType.SPORT_UFC;
    }

    @Override
    public void load() {
        final String wikipagePrefix = "https://en.wikipedia.org";
        final String url = wikipagePrefix + "/wiki/List_of_UFC_events";
        final Document doc = getDocument(url);
        if(doc != null) {
            final Elements table = doc.select("table.sortable");
            for(Element tableElement : table) {
                if(tableElement.attr("id").equals("Scheduled_events")) {
                    final Elements elements = tableElement.select("tbody tr");
                    elements.remove(0);
                    if(elements.size() > 0) {
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
                                    putPreUpcomingEvent(id, preUpcomingEvent);
                                }
                            }
                        });
                    }
                    break;
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
