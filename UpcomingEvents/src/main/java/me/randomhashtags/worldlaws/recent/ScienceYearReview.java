package me.randomhashtags.worldlaws.recent;

import me.randomhashtags.worldlaws.EventDate;
import me.randomhashtags.worldlaws.EventSource;
import me.randomhashtags.worldlaws.locale.JSONObjectTranslatable;
import me.randomhashtags.worldlaws.service.WikipediaEvent;
import me.randomhashtags.worldlaws.service.wikipedia.WikipediaDocument;
import me.randomhashtags.worldlaws.settings.Settings;
import me.randomhashtags.worldlaws.stream.CompletableFutures;
import me.randomhashtags.worldlaws.upcoming.LoadedUpcomingEventController;
import me.randomhashtags.worldlaws.upcoming.UpcomingEventType;
import me.randomhashtags.worldlaws.upcoming.events.ScienceYearReviewEvent;
import me.randomhashtags.worldlaws.upcoming.events.UpcomingEvent;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.time.LocalDate;
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
        final UpcomingEventType type = getType();
        final int daysInTheFuture = 7;
        final LocalDate now = LocalDate.now();
        final List<LocalDate> dates = new ArrayList<>(List.of(now));
        for(int i = 0; i <= daysInTheFuture; i++) {
            final LocalDate date = now.plusDays(i);
            dates.add(date);
        }

        final JSONObject pastJSON = getTodayEventsFromThePastJSON(dates.toArray(new LocalDate[dates.size()]));
        for(LocalDate date : dates) {
            final String dateString = EventDate.getDateString(date), title = "Today in Science";
            final String identifier = getEventDateIdentifier(dateString, title);
            final String[] dateValues = dateString.split("-");
            final JSONObjectTranslatable json = new JSONObjectTranslatable();
            String imageURL = null;
            List<String> mentionedCountries = new ArrayList<>();
            for(String pastDateString : pastJSON.keySet()) {
                if(pastDateString.startsWith(dateValues[0] + "-") && pastDateString.endsWith("-" + dateValues[2])) {
                    final String[] pastDateValues = pastDateString.split("-");
                    final JSONArray array = pastJSON.getJSONArray(pastDateString);
                    if(imageURL == null) {
                        final JSONObject first = array.getJSONObject(0);
                        if(first.has("images")) {
                            imageURL = first.getJSONArray("images").getString(0);
                        }
                    }
                    for(Object obj : array) {
                        final JSONObject eventJSON = (JSONObject) obj;
                        final JSONObject mentionedSovereignStatesJSON = eventJSON.optJSONObject("mentionedSovereignStates", null);
                        if(mentionedSovereignStatesJSON != null) {
                            final Set<String> keys = new HashSet<>(mentionedSovereignStatesJSON.keySet());
                            keys.removeIf(mentionedCountries::contains);
                            mentionedCountries.addAll(keys);
                        }
                    }
                    final String year = "" + Integer.parseInt(pastDateValues[1]);
                    json.put(year, array);
                    json.addTranslatedKey(year);
                }
            }
            final ScienceYearReviewEvent event = new ScienceYearReviewEvent(EventDate.valueOfDateString(dateString), title, null, imageURL, json);
            final int amount = json.keySet().size();
            putLoadedPreUpcomingEvent(event.toPreUpcomingEventJSON(type, identifier, amount + " Event" + (amount > 1 ? "s" : ""), mentionedCountries));
            putUpcomingEvent(identifier, event);
        }
    }

    @Override
    public UpcomingEvent parseUpcomingEvent(JSONObject json) {
        return new ScienceYearReviewEvent(json);
    }

    private JSONObject getTodayEventsFromThePastJSON(LocalDate...dates) {
        final JSONObject json = new JSONObject();
        final List<LocalDate> loadImageDates = Arrays.asList(dates);
        final List<Integer> years = Settings.ServerValues.UpcomingEvents.getScienceYearReviewYears();
        new CompletableFutures<Integer>().stream(years, targetYear -> {
            final JSONObject datesJSON = getJSON(targetYear, loadImageDates);
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
        final JSONObject json = getJSON(year, null);
        return json != null ? json.toString() : null;
    }
    private JSONObject getJSON(int year, List<LocalDate> loadImageDates) {
        if(!dateJSONs.containsKey(year)) {
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
                final JSONObject json = parseMonthEventsJSON(loadImageDates, tables, year, references);
                dateJSONs.put(year, json);
            }
        }
        return dateJSONs.get(year);
    }
    private JSONObject parseMonthEventsJSON(List<LocalDate> loadImageDates, Elements tables, int year, HashMap<String, EventSource> references) {
        final String identifier = "ScienceYearReview,year=" + year;
        final String targetQuery = year < 2020 ? "li" : "tbody tr td ul li";
        final JSONObject json = new JSONObject();
        final List<String> loadImageDateStrings = new ArrayList<>();
        if(loadImageDates != null) {
            for(LocalDate date : loadImageDates) {
                final String dateString = getEventDateString(year, date.getMonth(), date.getDayOfMonth());
                loadImageDateStrings.add(dateString);
            }
        }
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
                if(loadImageDateStrings.contains(dateString)) {
                    for(WikipediaEvent event : events) {
                        event.getImages(1);
                    }
                }
                for(WikipediaEvent event : events) {
                    json.getJSONArray(dateString).put(event);
                }
            }
        }
        return json;
    }
}
