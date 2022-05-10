package me.randomhashtags.worldlaws.service;

import me.randomhashtags.worldlaws.EventDate;
import me.randomhashtags.worldlaws.EventSource;
import me.randomhashtags.worldlaws.RestAPI;
import me.randomhashtags.worldlaws.country.WLCountry;
import me.randomhashtags.worldlaws.locale.JSONObjectTranslatable;
import me.randomhashtags.worldlaws.service.wikipedia.WikipediaDocument;
import me.randomhashtags.worldlaws.stream.CompletableFutures;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.select.Elements;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public enum CountryYearReview implements RestAPI {
    INSTANCE;

    public JSONObject getTodayEventsFromThePastJSON(WLCountry country, LocalDate...dates) {
        final JSONObjectTranslatable json = new JSONObjectTranslatable();
        final List<Integer> years = Arrays.asList(2022, 2021, 2020, 2019, 2018, 2017, 2016, 2015, 2014, 2013, 2012, 2011, 2010);
        new CompletableFutures<Integer>().stream(years, targetYear -> {
            final JSONObject datesJSON = getCountryEventsJSON(targetYear, country);
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
    public JSONObjectTranslatable getCountryEventsJSON(int year, WLCountry country) {
        final String countryName = (country.getShortNamePrefix() + country.getShortName()).replace(" ", "_");
        final String identifier = "CountryYearReview,countryName=" + countryName + ",year=" + year;
        final String url = "https://en.wikipedia.org/wiki/" + year + "_in_" + countryName;
        final WikipediaDocument doc = new WikipediaDocument(url);
        final Elements elements = doc.selectFirst("div.mw-parser-output").children();
        elements.removeIf(element -> element.hasClass("thumb"));
        final Elements dayElements = elements.select("ul li");
        if(!dayElements.isEmpty()) {
            final HashMap<String, EventSource> references = doc.getReferences(identifier);
            final JSONObjectTranslatable json = new JSONObjectTranslatable();
            final HashMap<EventDate, List<WikipediaEvent>> map = WikipediaEvent.parseMonthEvents(identifier, year, country, dayElements, references);
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
            return json;
        }
        return null;
    }
}
