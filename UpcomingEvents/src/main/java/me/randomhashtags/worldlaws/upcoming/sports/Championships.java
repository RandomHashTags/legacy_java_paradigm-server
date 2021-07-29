package me.randomhashtags.worldlaws.upcoming.sports;

import me.randomhashtags.worldlaws.CompletionHandler;
import me.randomhashtags.worldlaws.PreUpcomingEvent;
import me.randomhashtags.worldlaws.WLUtilities;
import me.randomhashtags.worldlaws.upcoming.UpcomingEventController;
import me.randomhashtags.worldlaws.upcoming.UpcomingEventType;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.time.LocalDate;
import java.time.Month;
import java.util.HashMap;
import java.util.HashSet;

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
            final LocalDate date = LocalDate.now();
            final int month = date.getMonth().getValue(), previousMonth = date.minusMonths(1).getMonth().getValue();
            final Elements tables = doc.select("h3 + table.wikitable");
            loadPreEventsFrom(tables.get(month-1));
            loadPreEventsFrom(tables.get(previousMonth-1));
        }
    }

    private void loadPreEventsFrom(Element table) {
        final Elements trs = table.select("tbody tr");
        trs.removeIf(tr -> {
            final Elements tds = tr.select("td");
            final String text = tds.get(trs.size()-1).text().toLowerCase();
            return text.equals("postponed") || text.contains("postponed to") || text.contains("cancelled") || text.contains("canceled");
        });
        final String hyphen = "–", space = " ", urlPrefix = "https://en.wikipedia.org";
        for(Element tr : trs) {
            boolean finished = false;
            final Elements tds = tr.select("td");
            String day = tds.get(0).text();
            Month endingMonth = null;
            if(day.contains(space)) {
                final String[] values = day.split(space);
                endingMonth = WLUtilities.valueOfMonthFromInput(values[values.length-1]);
                day = values[0];
            }
            if(day.contains(hyphen)) {
                final String[] values = day.split(hyphen);
                final int startingDay = Integer.parseInt(values[0]), endingDay = Integer.parseInt(values[1]);
            }

            final String sport = tds.get(1).text();

            final Element eventElement = tds.get(2);
            final String event = eventElement.text();
            final Elements eventElementLinks = eventElement.select("a[href]");
            String eventURL = null;
            final HashSet<String> countries = new HashSet<>();
            if(!eventElementLinks.isEmpty()) {
                final Elements flagIconElements = eventElement.select("span.flagicon");
                for(Element flagIcon : flagIconElements) {
                    final String[] href = flagIcon.selectFirst("a[href]").attr("href").split("/");
                    final String country = href[href.length-1];
                    countries.add(country.toLowerCase().replace(" ", ""));
                }
                eventURL = urlPrefix + eventElementLinks.get(flagIconElements.size()).attr("href");
            }

            final String winners = tds.get(tds.size()-1).text();
            if(!winners.isEmpty()) {
                finished = !winners.contains("postponed from");
            }

            final PreUpcomingEvent preUpcomingEvent = new PreUpcomingEvent(null, event, null, null);
        }
    }

    @Override
    public void loadUpcomingEvent(String id, CompletionHandler handler) {

    }
}
