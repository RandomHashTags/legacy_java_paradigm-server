package me.randomhashtags.worldlaws.upcoming.space;

import me.randomhashtags.worldlaws.EventSources;
import me.randomhashtags.worldlaws.WLUtilities;
import me.randomhashtags.worldlaws.service.wikipedia.WikipediaDocument;
import me.randomhashtags.worldlaws.upcoming.LoadedUpcomingEventController;
import me.randomhashtags.worldlaws.upcoming.UpcomingEventType;
import me.randomhashtags.worldlaws.upcoming.events.LunarEclipseEvent;
import me.randomhashtags.worldlaws.upcoming.events.UpcomingEvent;
import org.json.JSONObject;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.time.Month;
import java.time.format.DateTimeFormatter;

public final class LunarEclipses extends LoadedUpcomingEventController {

    @Override
    public UpcomingEventType getType() {
        return UpcomingEventType.SPACE_LUNAR_ECLIPSE;
    }

    @Override
    public void load() {
        final String url = "https://en.wikipedia.org/wiki/List_of_21st-century_lunar_eclipses";
        final WikipediaDocument wikiDoc = new WikipediaDocument(url);
        if(wikiDoc.exists()) {
            final Element table = wikiDoc.selectFirst("table.wikitable");
            if(table != null) {
                final Elements elements = table.select("tbody tr");
                elements.remove(0);
                elements.remove(0);
                elements.removeIf(element -> element.hasAttr("bgcolor") && element.attr("bgcolor").equals("#e0e0ff"));
                if(!elements.isEmpty()) {
                    final UpcomingEventType type = UpcomingEventType.SPACE_LUNAR_ECLIPSE;
                    for(Element tr : elements) {
                        final Elements tds = tr.select("td");
                        final LunarEclipseEvent event = parseEvent(tds);
                        if(event != null) {
                            final String identifier = event.getIdentifier();
                            putLoadedPreUpcomingEvent(event.toPreUpcomingEventJSON(type, identifier, null));
                            putUpcomingEvent(identifier, event);
                        }
                    }
                }
            }
        }
    }

    private LunarEclipseEvent parseEvent(Elements tds) {
        final String[] dateValues = tds.get(0).text().split(" ");
        if(dateValues.length != 3) {
            return null;
        }
        final int year = Integer.parseInt(dateValues[0]), day = Integer.parseInt(dateValues[2]);
        final Month month = WLUtilities.valueOfMonthFromInput(dateValues[1]);
        final int monthValue = month.getValue();

        String timeGreatest = tds.get(1).text() + ":00";
        if(timeGreatest.split(":")[0].length() == 1) {
            timeGreatest = "0" + timeGreatest;
        }
        timeGreatest = year + "-" + (monthValue < 10 ? "0" : "") + monthValue + "-" + (day < 10 ? "0" : "") + day + "T" + timeGreatest + "+00:00";
        final long timeGreatestMilliseconds = WLUtilities.parseDateFormatToMilliseconds(DateTimeFormatter.ISO_OFFSET_DATE_TIME, timeGreatest);

        final String type = tds.get(2).text();
        final String orbitalNode = tds.get(3).text();
        final String saros = tds.get(4).text();
        final String gamma = tds.get(5).text();
        final String magnitudePenumbra = tds.get(6).text(), magnitudeUmbra = tds.get(7).text();
        final String durationPartialString = tds.get(8).text(), durationTotalString = tds.get(9).text();
        final int durationPartial = durationPartialString.isEmpty() ? 0 : Integer.parseInt(durationPartialString), durationTotal = durationTotalString.isEmpty() ? 0 : Integer.parseInt(durationTotalString);

        final Element chartElement = tds.get(16);
        final Element imageElement = chartElement.selectFirst("img");
        String imageURL = null;
        if(imageElement != null) {
            imageURL = "https:" + imageElement.attr("src").replace("/40px-", "/%quality%px-");
        }

        final String title = type + " Lunar Eclipse";
        final String description = null;
        final EventSources sources = new EventSources(
                //new EventSource("Wikipedia: ", "")
        );
        return new LunarEclipseEvent(timeGreatestMilliseconds, title, description, imageURL, timeGreatestMilliseconds, orbitalNode, saros, gamma, magnitudePenumbra, magnitudeUmbra, durationPartial, durationTotal, sources);
    }

    @Override
    public boolean isExactTime() {
        return true;
    }

    @Override
    public UpcomingEvent parseUpcomingEvent(JSONObject json) {
        return new LunarEclipseEvent(json);
    }
}
