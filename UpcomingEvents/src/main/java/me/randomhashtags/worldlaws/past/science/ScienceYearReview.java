package me.randomhashtags.worldlaws.past.science;

import me.randomhashtags.worldlaws.EventDate;
import me.randomhashtags.worldlaws.EventSource;
import me.randomhashtags.worldlaws.WLUtilities;
import me.randomhashtags.worldlaws.service.WikipediaDocument;
import me.randomhashtags.worldlaws.service.WikipediaEvent;
import me.randomhashtags.worldlaws.settings.Settings;
import me.randomhashtags.worldlaws.stream.CompletableFutures;
import me.randomhashtags.worldlaws.upcoming.LoadedUpcomingEventController;
import me.randomhashtags.worldlaws.upcoming.UpcomingEventType;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        new CompletableFutures<Integer>().stream(years, targetYear -> {
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
            final WikipediaDocument doc = new WikipediaDocument(url);
            if(doc.exists()) {
                final HashMap<String, EventSource> references = doc.getReferences("ScienceYearReview,year=" + year);
                Elements tables = null;
                if(year < 2020) {
                    tables = doc.selectFirst("div.mw-parser-output").select("ul");
                } else {
                    tables = doc.select("h3 + table.wikitable");
                }
                final JSONObject json = parseMonthEventsJSON(tables, year, references);
                dateJSONs.put(year, json);
            }
        }
        return dateJSONs.get(year);
    }
    private JSONObject parseMonthEventsJSON(Elements tables, int year, HashMap<String, EventSource> references) {
        final String identifier = "ScienceYearReview,year=" + year;
        final String targetQuery = year < 2020 ? "li" : "tbody tr td ul li";
        final JSONObject json = new JSONObject();
        for(Element table : tables) {
            final Elements dayElements = table.select(targetQuery);
            final HashMap<EventDate, List<WikipediaEvent>> map = WikipediaEvent.parseMonthEvents(identifier, year, null, dayElements, references);
            for(Map.Entry<EventDate, List<WikipediaEvent>> entry : map.entrySet()) {
                final EventDate date = entry.getKey();
                final String dateString = date.getDateString();
                if(!json.has(dateString)) {
                    json.put(dateString, new JSONArray());
                }
                final List<WikipediaEvent> events = entry.getValue();
                for(WikipediaEvent event : events) {
                    json.getJSONArray(dateString).put(event);
                }
            }
        }
        return json;
    }



}
