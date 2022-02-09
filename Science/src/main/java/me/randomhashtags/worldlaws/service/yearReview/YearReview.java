package me.randomhashtags.worldlaws.service.yearReview;

import me.randomhashtags.worldlaws.*;
import me.randomhashtags.worldlaws.country.WLCountry;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.time.Month;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public enum YearReview implements RestAPI, Jsonable, Jsoupable {
    INSTANCE;

    public void get(int year) {
        final boolean isCurrentYear = WLUtilities.getTodayYear() == year;
        final String url = "https://en.wikipedia.org/wiki/" + year + "_in_science";
        final Document doc = getDocument(url);
        if(doc != null) {
            final Elements monthElements = doc.select("h3");
        }
    }

    private HashMap<EventDate, HashSet<ScienceEvent>> parseMonthEvents(Elements dayElements) {
        final HashMap<EventDate, HashSet<ScienceEvent>> map = new HashMap<>();
        for(Element dayElement : dayElements) {
            final EventDate date = new EventDate(Month.JANUARY, 1, 1);
            final HashSet<ScienceEvent> events = parseEvents(dayElement);
            map.put(date, events);
        }
        final WLCountry[] countries = WLCountry.values();
        for(Map.Entry<EventDate, HashSet<ScienceEvent>> bruh : map.entrySet()) {
            for(ScienceEvent event : bruh.getValue()) {
                event.updateMentionedCountries(countries);
            }
        }
        return map;
    }
    private HashSet<ScienceEvent> parseEvents(Element listElement) {
        final HashSet<ScienceEvent> events = new HashSet<>();
        final Element innerList = listElement.selectFirst("ul");
        if(innerList != null) {
            for(Element element : innerList.select("li")) {
                final String text = element.text();
                final EventSources externalLinks = getExternalLinks(element), sources = getSources(element);
                final ScienceEvent event = new ScienceEvent(text, externalLinks, sources);
                events.add(event);
            }
        } else {
        }
        return events;
    }
    private EventSources getExternalLinks(Element element) {
        final EventSources sources = new EventSources();
        final Elements links = element.select("a[href]");
        final String domain = "https://en.wikipedia.org";
        for(Element href : links) {
            final String text = href.text();
            final EventSource source = new EventSource("Wikipedia: " + text, domain + href.attr("href"));
            sources.add(source);
        }
        return sources.isEmpty() ? null : sources;
    }
    private EventSources getSources(Element element) {
        final EventSources sources = new EventSources();
        final Elements superscripts = element.select("sup.reference");
        for(Element superscript : superscripts) {

        }
        return sources;
    }
}
