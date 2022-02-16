package me.randomhashtags.worldlaws.service.yearReview;

import me.randomhashtags.worldlaws.*;
import me.randomhashtags.worldlaws.country.WLCountry;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.time.LocalDate;
import java.time.Month;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public enum ScienceYearReview implements RestAPI, Jsonable, Jsoupable {
    INSTANCE;

    public String getTodayEventsFromThePast(LocalDate date) {
        final int year = date.getYear(), day = date.getDayOfMonth();
        final Month month = date.getMonth();
        final JSONObject json = new JSONObject();
        for(int i = 1; i <= 5; i++) {
            final int targetYear = year - i;
            final JSONObject datesJSON = getJSON(targetYear);
            if(datesJSON != null) {
                final String dateString = EventDate.getDateString(targetYear, day, month);
                if(datesJSON.has(dateString)) {
                    json.put(dateString, datesJSON.getJSONArray(dateString));
                }
            }
        }
        return json.toString();
    }
    public String get(int year) {
        final JSONObject json = getJSON(year);
        return json != null ? json.toString() : null;
    }
    private JSONObject getJSON(int year) {
        final boolean isCurrentYear = WLUtilities.getTodayYear() == year;
        final String url = "https://en.wikipedia.org/wiki/" + year + "_in_science";
        final Document doc = getDocument(url);
        if(doc != null) {
            final WLCountry[] countries = WLCountry.values();
            final Element reflistElement = doc.selectFirst("div.reflist");
            final HashMap<String, EventSource> references = parseReferences(year, reflistElement);
            Elements tables = year < 2020 ? doc.selectFirst("div.mw-parser-output h3").siblingElements() : doc.select("h3 + table.wikitable");
            if(year < 2020) {
                tables.removeIf(element -> element.hasClass("thumb"));
                tables = tables.select("ul");
            }
            return parseMonthEventsJSON(tables, year, countries, references);
        }
        return null;
    }
    private JSONObject parseMonthEventsJSON(Elements tables, int year, WLCountry[] countries, HashMap<String, EventSource> references) {
        final String targetQuery = year < 2020 ? "li" : "tbody tr td ul li";
        final JSONObject json = new JSONObject();
        for(Element element : tables) {
            final Elements dayElements = element.select(targetQuery);
            final HashMap<EventDate, HashSet<ScienceEvent>> map = parseMonthEvents(year, countries, dayElements, references);
            for(Map.Entry<EventDate, HashSet<ScienceEvent>> entry : map.entrySet()) {
                final EventDate date = entry.getKey();
                final String dateString = date.getDateString();
                if(!json.has(dateString)) {
                    json.put(dateString, new JSONArray());
                }
                final HashSet<ScienceEvent> events = entry.getValue();
                for(ScienceEvent event : events) {
                    json.getJSONArray(dateString).put(event);
                }
            }
        }
        return json;
    }

    private HashMap<String, EventSource> parseReferences(int year, Element reflistElement) {
        final HashMap<String, EventSource> references = new HashMap<>();
        for(Element listElement : reflistElement.select("ol.references li")) {
            final String[] numberValues = listElement.attr("id").split("-");
            final String number = numberValues[numberValues.length-1];
            final Element referenceTextElement = listElement.selectFirst("span.reference-text");
            Element sourceElement = referenceTextElement.selectFirst("cite.citation");
            if(sourceElement == null) {
                sourceElement = referenceTextElement;
                WLLogger.logInfo("ScienceYearReview;parseReferences;referenceElement == null;year=" + year + ";number=" + number);
            }
            final Element ahref = sourceElement.selectFirst("a.external");
            if(ahref == null) {
                WLLogger.logInfo("ScienceYearReview;parseReferences;ahref == null;year=" + year + ";number=" + number);
            } else {
                final String url = ahref.attr("href");
                String title = ahref.text();
                if(sourceElement.text().toLowerCase().startsWith("clinical trial number")) {
                    title = "Clinical Trial Number " + title;
                }
                final Element italicElement = sourceElement.selectFirst("i");
                String siteName = italicElement != null ? italicElement.text() : "Unknown Publisher";
                if(italicElement == null) {
                    final String[] values = sourceElement.text().split("\\. ");
                    if(values.length >= 2) {
                        if(values[1].matches("[0-9]+ [a-zA-Z]+ [0-9]+")) {
                            if(values[0].contains(":")) {
                                siteName = null;
                            } else if(values[0].contains(" - ")) {
                                final String[] test = values[0].split(" - ");
                                siteName = test[test.length-1];
                            }
                            if(siteName != null) {
                                siteName = siteName.replace("\"", "");
                            }
                            WLLogger.logInfo("ScienceYearReview;parseReferences;italicElement == null;year=" + year + ";number=" + number + ";values[1]=" + values[1] + ";siteName=" + siteName);
                        } else {
                            siteName = values[1];
                        }
                    }
                }
                final String realSiteName = (siteName != null ? siteName + ": " : "") + title;
                final EventSource source = new EventSource(realSiteName, url);
                references.put(number, source);
            }

        }
        return references;
    }
    private HashMap<EventDate, HashSet<ScienceEvent>> parseMonthEvents(int year, WLCountry[] countries, Elements dayElements, HashMap<String, EventSource> references) {
        final HashMap<EventDate, HashSet<ScienceEvent>> map = new HashMap<>();
        for(Element dayElement : dayElements) {
            final String dayText = dayElement.text();
            final String[] textValues = dayText.split(" ");
            if(textValues[0].matches("[0-9]+") && textValues.length > 1) {
                final Month month = WLUtilities.valueOfMonthFromInput(textValues[1]);
                if(month != null) {
                    final int day = Integer.parseInt(textValues[0]);
                    final EventDate date = new EventDate(month, day, year);
                    final HashSet<ScienceEvent> events = parseEvents(dayElement, references);
                    map.put(date, events);
                }
            }
        }
        for(Map.Entry<EventDate, HashSet<ScienceEvent>> bruh : map.entrySet()) {
            for(ScienceEvent event : bruh.getValue()) {
                event.updateMentionedCountries(countries);
            }
        }
        return map;
    }
    private HashSet<ScienceEvent> parseEvents(Element listElement, HashMap<String, EventSource> references) {
        final HashSet<ScienceEvent> events = new HashSet<>();
        final Element innerList = listElement.selectFirst("ul");
        if(innerList != null) {
            for(Element element : innerList.select("li")) {
                final String text = LocalServer.removeWikipediaReferences(element.text());
                final EventSources externalLinks = getExternalLinks(element), sources = getSources(references, element);
                final ScienceEvent event = new ScienceEvent(text, externalLinks, sources);
                events.add(event);
            }
        } else {
            String text = LocalServer.removeWikipediaReferences(listElement.text());
            final int length = text.split(" – ")[0].length() + 3;
            if(text.length() - length > 0) {
                text = text.substring(length);
                final EventSources externalLinks = getExternalLinks(listElement), sources = getSources(references, listElement);
                final ScienceEvent event = new ScienceEvent(text, externalLinks, sources);
                events.add(event);
            } else {
                WLLogger.logInfo("ScienceYearReview;parseEvents;text=" + text);
            }
        }
        return events;
    }
    private EventSources getExternalLinks(Element element) {
        final EventSources sources = new EventSources();
        final Elements links = element.select("a[href]");
        links.removeIf(test -> test.attr("href").startsWith("#cite_note-"));
        final String wikipediaDomain = "https://en.wikipedia.org";
        for(Element href : links) {
            final String text = href.attr("href");
            final String[] values = text.split("/");
            final String homepageURL = text.startsWith("http") ? text : wikipediaDomain + text;
            final EventSource source = new EventSource("Wikipedia: " + values[values.length-1].replace("_", " "), homepageURL);
            sources.add(source);
        }
        return sources.isEmpty() ? null : sources;
    }
    private EventSources getSources(HashMap<String, EventSource> references, Element element) {
        final EventSources sources = new EventSources();
        final Elements superscripts = element.select("sup.reference");
        for(Element superscript : superscripts) {
            final String number = superscript.text().replace("[", "").replace("]", "");
            if(references.containsKey(number)) {
                sources.add(references.get(number));
            }
        }
        return sources;
    }
}
