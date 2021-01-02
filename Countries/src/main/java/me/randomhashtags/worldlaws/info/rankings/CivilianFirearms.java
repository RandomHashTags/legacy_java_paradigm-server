package me.randomhashtags.worldlaws.info.rankings;

import me.randomhashtags.worldlaws.*;
import me.randomhashtags.worldlaws.info.rankings.CountryRankingInfoValue;
import me.randomhashtags.worldlaws.info.rankings.CountryRankingInfoValueOther;
import me.randomhashtags.worldlaws.info.rankings.CountryRankingService;
import me.randomhashtags.worldlaws.location.CountryInfo;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.*;
import java.util.logging.Level;

public enum CivilianFirearms implements CountryRankingService {
    INSTANCE;

    private String rankedJSON;
    private HashMap<String, String> countries;

    @Override
    public CountryInfo getInfo() {
        return CountryInfo.RANKING_CIVILIAN_FIREARMS;
    }

    @Override
    public void getRankedJSON(CompletionHandler handler) {
        if(rankedJSON != null) {
            handler.handle(rankedJSON);
        } else {
            refresh(new CompletionHandler() {
                @Override
                public void handle(Object object) {
                    handler.handle(rankedJSON);
                }
            });
        }
    }

    @Override
    public void getValue(String countryBackendID, CompletionHandler handler) {
        if(countries != null) {
            final String value = getValue(countryBackendID);
            handler.handle(value);
        } else {
            refresh(new CompletionHandler() {
                @Override
                public void handle(Object object) {
                    final String value = getValue(countryBackendID);
                    handler.handle(value);
                }
            });
        }
    }
    private String getValue(String countryBackendID) {
        final String value = countries.getOrDefault(countryBackendID, "null");
        if(value.equals("null")) {
            WLLogger.log(Level.WARNING, "CivilianFirearms - missing for country \"" + countryBackendID + "\"!");
        }
        return value;
    }

    private void refresh(CompletionHandler handler) {
        final long started = System.currentTimeMillis();
        countries = new HashMap<>();
        final String url = "https://en.wikipedia.org/wiki/Estimated_number_of_civilian_guns_per_capita_by_country";
        final Document doc = getDocument(url);
        if(doc != null) {
            final Elements elements = doc.select("div.mw-parser-output table.wikitable").get(0).select("tbody tr");
            elements.remove(0);
            elements.remove(0);
            final EventSource wikipedia = new EventSource("Wikipedia: Estimated number of civilian guns per capita by country", url);
            final EventSources sources = new EventSources(wikipedia);
            final int maxWorldRank = elements.size(), yearOfData = 2017;
            final Collection<CountryRankingInfoValue> list = new HashSet<>();
            for(Element element : elements) {
                final Elements tds = element.select("td");
                final String estimate = tds.get(6).text().replace(",", ""), registeredFirearms = tds.get(8).text().replace(",", ""), unregisteredFirearms = tds.get(9).text().replace(",", "");
                final int worldRank = Integer.parseInt(tds.get(0).text());
                final int estimateInCivilianPossession = estimate.matches("[0-9]+") ? Integer.parseInt(estimate) : -1;
                final int registered = registeredFirearms.matches("[0-9]+") ? Integer.parseInt(registeredFirearms) : -1;
                final int unregistered = unregisteredFirearms.matches("[0-9]+") ? Integer.parseInt(unregisteredFirearms) : -1;
                final String country = tds.get(1).text().toLowerCase().replace(" ", "");
                final float estimatePer100 = Float.parseFloat(tds.get(2).text());
                final int defcon = -1;
                final CountryRankingInfoValue value = new CountryRankingInfoValue(defcon, country, maxWorldRank, worldRank, yearOfData, estimatePer100, NumberType.FLOAT, true, "Civilian Firearms", " per 100 persons", sources);
                final List<CountryRankingInfoValueOther> values = new ArrayList<>();
                values.add(new CountryRankingInfoValueOther(estimateInCivilianPossession, NumberType.INTEGER, "Estimate in Civilian Possession", null));
                values.add(new CountryRankingInfoValueOther(registered, NumberType.INTEGER, "Registered", null));
                values.add(new CountryRankingInfoValueOther(unregistered, NumberType.INTEGER, "Unregistered", null));
                value.setOtherValues(values);
                list.add(value);
                countries.put(country, value.toString());
            }
            rankedJSON = toRankedJSON(list);
            WLLogger.log(Level.INFO, "CivilianFirearms - refreshed (took " + (System.currentTimeMillis()-started) + "ms)");
            handler.handle(null);
        }
    }
}
