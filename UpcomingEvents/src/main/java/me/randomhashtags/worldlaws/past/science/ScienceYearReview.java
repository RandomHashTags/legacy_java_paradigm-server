package me.randomhashtags.worldlaws.past.science;

import me.randomhashtags.worldlaws.*;
import me.randomhashtags.worldlaws.country.WLCountry;
import me.randomhashtags.worldlaws.settings.Settings;
import me.randomhashtags.worldlaws.stream.ParallelStream;
import me.randomhashtags.worldlaws.upcoming.LoadedUpcomingEventController;
import me.randomhashtags.worldlaws.upcoming.UpcomingEventType;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.time.LocalDate;
import java.time.Month;
import java.util.*;

public final class ScienceYearReview extends LoadedUpcomingEventController {

    private final HashMap<Integer, JSONObject> dateJSONs;

    public ScienceYearReview() {
        dateJSONs = new HashMap<>();
    }

    @Override
    public UpcomingEventType getType() {
        return UpcomingEventType.SCIENCE_YEAR_REVIEW;
    }

    @Override
    public void load() {
        final int daysInTheFuture = 7;
        final LocalDate now = LocalDate.now();
        final List<LocalDate> dates = new ArrayList<>(List.of(now));
        for(int i = 0; i <= daysInTheFuture; i++) {
            final LocalDate date = now.plusDays(i);
            dates.add(date);
        }

        final JSONObject pastJSON = getTodayEventsFromThePastJSON(dates.toArray(new LocalDate[dates.size()]));
        for(LocalDate date : dates) {
            final String dateString = EventDate.getDateString(date);
            final String identifier = getEventDateIdentifier(dateString, "Today in Science");
            final String[] dateValues = dateString.split("-");
            final JSONObject json = new JSONObject();
            for(String key : pastJSON.keySet()) {
                if(key.startsWith(dateValues[0] + "-") && key.endsWith("-" + dateValues[2])) {
                    json.put(key, pastJSON.getJSONArray(key));
                }
            }
            final String string = json.toString(), realString = string.substring(1, string.length()-1);
            putLoadedPreUpcomingEvent(identifier, realString);
            //putUpcomingEvent(identifier, realString);
        }
    }

    private JSONObject getTodayEventsFromThePastJSON(LocalDate...dates) {
        final JSONObject json = new JSONObject();
        final List<Integer> years = Settings.ServerValues.UpcomingEvents.getScienceYearReviewYears();
        new ParallelStream<Integer>().stream(years, targetYear -> {
            final JSONObject datesJSON = getJSON(targetYear);
            if(datesJSON != null) {
                for(LocalDate date : dates) {
                    final String dateString = EventDate.getDateString(targetYear, date.getDayOfMonth(), date.getMonth());
                    if(datesJSON.has(dateString)) {
                        json.put(dateString, datesJSON.getJSONArray(dateString));
                    }
                }
            }
        });
        return json;
    }
    public String get(int year) {
        final JSONObject json = getJSON(year);
        return json != null ? json.toString() : null;
    }
    private JSONObject getJSON(int year) {
        if(!dateJSONs.containsKey(year)) {
            final boolean isCurrentYear = WLUtilities.getTodayYear() == year;
            final String url = "https://en.wikipedia.org/wiki/" + year + "_in_science";
            final Document doc = getDocument(url);
            if(doc != null) {
                final WLCountry[] countries = WLCountry.values();
                final Element reflistElement = doc.selectFirst("div.reflist");
                final HashMap<String, EventSource> references = parseReferences(year, reflistElement);
                Elements tables = null;
                if(year < 2020) {
                    tables = doc.selectFirst("div.mw-parser-output").select("ul");
                } else {
                    tables = doc.select("h3 + table.wikitable");
                }
                final JSONObject json = parseMonthEventsJSON(tables, year, countries, references);
                dateJSONs.put(year, json);
            }
        }
        return dateJSONs.get(year);
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
        final Elements listElements = reflistElement.select("ol.references li");
        for(Element listElement : listElements) {
            final String[] numberValues = listElement.attr("id").split("-");
            final String number = numberValues[numberValues.length-1];
            final Element referenceTextElement = listElement.selectFirst("span.reference-text");
            Element sourceElement = referenceTextElement.selectFirst("cite.citation");
            if(sourceElement == null) {
                sourceElement = referenceTextElement;
            }
            final Element ahref = sourceElement.selectFirst("a.external");
            if(ahref != null) {
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
            } else {
                WLLogger.logInfo("ScienceYearReview;parseReferences;ahref == null;year=" + year + ";number=" + number);
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
                    final HashSet<ScienceEvent> events = parseEvents(year, dayElement, references);
                    if(!events.isEmpty()) {
                        map.put(date, events);
                    }
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
    private HashSet<ScienceEvent> parseEvents(int year, Element listElement, HashMap<String, EventSource> references) {
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
            if(!text.contains(" (b. ") && !text.contains(" (born ")) {
                if(text.contains(" – ")) {
                    final String targetDateString = text.split(" – ")[0];
                    final int length = targetDateString.length() + 3;
                    if(text.length() - length > 0) {
                        text = text.substring(length);
                        final EventSources externalLinks = getExternalLinks(listElement), sources = getSources(references, listElement);
                        final ScienceEvent event = new ScienceEvent(text, externalLinks, sources);
                        events.add(event);
                    } else {
                        WLLogger.logWarning("ScienceYearReview - parseEvents;year=" + year + ";text=" + text);
                    }
                } else if(text.contains(" - ")) {
                    WLLogger.logWarning("ScienceYearReview - parseEvents;year=" + year + ";wikipedia text needs fixing=\"" + text + "\"");
                }
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
