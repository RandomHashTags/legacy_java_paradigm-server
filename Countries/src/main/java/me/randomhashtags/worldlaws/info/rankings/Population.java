package me.randomhashtags.worldlaws.info.rankings;

import me.randomhashtags.worldlaws.*;
import me.randomhashtags.worldlaws.info.rankings.CountryRankingInfoValue;
import me.randomhashtags.worldlaws.info.rankings.CountryRankingService;
import me.randomhashtags.worldlaws.location.CountryInfo;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.logging.Level;

public enum Population implements CountryRankingService {
    INSTANCE;

    private String rankedJSON;
    private HashMap<String, String> countries;

    @Override
    public CountryInfo getInfo() {
        return CountryInfo.RANKING_POPULATION;
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
            WLLogger.log(Level.WARNING, "Population - missing for country \"" + countryBackendID + "\"!");
        }
        return value;
    }

    private void refresh(CompletionHandler handler) {
        final long started = System.currentTimeMillis();
        countries = new HashMap<>();
        final String url = "https://en.wikipedia.org/wiki/List_of_countries_and_dependencies_by_population";
        final Document doc = getDocument(url);
        if(doc != null) {
            final Elements elements = doc.select("div.mw-parser-output table.wikitable").get(0).select("tbody tr");
            elements.remove(0);
            final int maxWorldRank = elements.size(), yearOfData = 2020;
            final EventSource wikipedia = new EventSource("Wikipedia: List of countries and dependencies by population", url);
            final Collection<CountryRankingInfoValue> list = new HashSet<>();
            final EventSources sources = new EventSources(wikipedia);
            for(Element element : elements) {
                final Elements tds = element.select("td");
                final String targetWorldRank = element.select("th").get(0).text();
                if(!targetWorldRank.equals("–")) {
                    try {
                        final int worldRank = Integer.parseInt(targetWorldRank);
                        final String country = tds.get(0).text().split("\\[")[0].toLowerCase().replace(" ", "");
                        final long population = Long.parseLong(tds.get(1).text().replace(",", ""));
                        final int defcon = -1;
                        final CountryRankingInfoValue value = new CountryRankingInfoValue(defcon, country, maxWorldRank, worldRank, yearOfData, population, NumberType.LONG, false, "World Population", "", sources);
                        list.add(value);
                        countries.put(country, value.toString());
                    } catch (Exception ignored) {
                    }
                }
            }
            rankedJSON = toRankedJSON(list);
            WLLogger.log(Level.INFO, "Population - refreshed (took " + (System.currentTimeMillis()-started) + "ms)");
            handler.handle(null);
        }
    }
}
