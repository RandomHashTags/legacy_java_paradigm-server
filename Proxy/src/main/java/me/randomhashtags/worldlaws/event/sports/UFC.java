package me.randomhashtags.worldlaws.event.sports;

import me.randomhashtags.worldlaws.CompletionHandler;
import me.randomhashtags.worldlaws.event.EventDate;
import me.randomhashtags.worldlaws.event.EventSource;
import me.randomhashtags.worldlaws.event.EventSources;
import me.randomhashtags.worldlaws.event.USAEventController;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.time.Month;

public enum UFC implements USAEventController {
    INSTANCE;

    private String json;

    @Override
    public String getIdentifier() {
        return "ufc";
    }

    @Override
    public void getUpcomingEvents(CompletionHandler handler) {
        if(json == null) {
            refreshUpcomingEvents(handler);
        } else {
            handler.handle(json);
        }
    }
    private void refreshUpcomingEvents(CompletionHandler handler) {
        final String url = "https://en.wikipedia.org/wiki/List_of_UFC_events";
        final Document doc = getDocument(url);
        if(doc != null) {
            final Elements table = doc.select("table.sortable");
            final StringBuilder builder = new StringBuilder("[");
            final EventSource source = new EventSource("Wikipedia", url);
            for(Element element : table) {
                if(element.attr("id").equals("Scheduled_events")) {
                    final Elements events = element.select("tbody tr");
                    events.remove(0);
                    boolean isFirst = true;
                    for(Element eventElement : events) {
                        final Elements rows = eventElement.select("td");
                        final String event = rows.get(0).text(), dateString = rows.get(1).text(), venue = rows.get(2).text(), location = rows.get(3).text();
                        final String[] dateValues = dateString.split(", "), dates = dateValues[0].split(" ");
                        final Month month = getMonth(dates[0]);
                        final int day = Integer.parseInt(dates[1]), year = Integer.parseInt(dateValues[1]);
                        final EventDate date = new EventDate(month, day, year);
                        final EventSources sources = new EventSources(source);
                        final SportEvent ufc = new SportEvent(date, event, venue, location, sources);
                        builder.append(isFirst ? "" : ",").append(ufc.toJSON());
                        isFirst = false;
                    }
                    break;
                }
            }
            builder.append("]");
            json = builder.toString();
            handler.handle(json);
        }
    }
    private Month getMonth(String fromPrefix) {
        switch (fromPrefix.toLowerCase()) {
            case "jan": return Month.JANUARY;
            case "feb": return Month.FEBRUARY;
            case "mar": return Month.MARCH;
            case "apr": return Month.APRIL;
            case "may": return Month.MAY;
            case "jun": return Month.JUNE;
            case "jul": return Month.JULY;
            case "aug": return Month.AUGUST;
            case "sep": return Month.SEPTEMBER;
            case "oct": return Month.OCTOBER;
            case "nov": return Month.NOVEMBER;
            case "dec": return Month.DECEMBER;
            default: return null;
        }
    }
}
