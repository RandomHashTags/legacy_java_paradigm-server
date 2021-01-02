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

public enum EconomicFreedomIndex implements CountryRankingService {
    INSTANCE;

    private String rankedJSON;
    private HashMap<String, String> countries;

    @Override
    public CountryInfo getInfo() {
        return CountryInfo.RANKING_ECONOMIC_FREEDOM_INDEX;
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
            WLLogger.log(Level.WARNING, "EconomicFreedomIndex - missing for country \"" + countryBackendID + "\"!");
        }
        return value;
    }

    private void refresh(CompletionHandler handler) {
        final long started = System.currentTimeMillis();
        countries = new HashMap<>();
        final String url = "https://en.wikipedia.org/wiki/Index_of_Economic_Freedom";
        final Document doc = getDocument(url);
        if(doc != null) {
            final Elements elements = doc.select("div.mw-parser-output table.wikitable").get(0).select("tbody tr");
            elements.remove(0);
            final EventSource wikipedia = new EventSource("Wikipedia: Index of Economic Freedom", url);
            final EventSources sources = new EventSources(wikipedia);
            final int maxWorldRank = elements.size(), yearOfData = 2020;
            final Collection<CountryRankingInfoValue> list = new HashSet<>();
            for(Element element : elements) {
                final Elements tds = element.select("td");
                final String country = tds.get(0).text().toLowerCase().replace(" ", "");
                final int worldRank = Integer.parseInt(tds.get(1).text());
                final float percent = Float.parseFloat(tds.get(2).text());
                final int defcon = percent < 60.00 ? 1 : percent < 65.00 ? 2 : percent < 70.00 ? 3 : percent < 80.00 ? 4 : 5;
                final CountryRankingInfoValue value = new CountryRankingInfoValue(defcon, country, maxWorldRank, worldRank, yearOfData, percent, NumberType.FLOAT, false, "Economic Freedom Index", " score", sources);
                list.add(value);
                countries.put(country, value.toString());
            }
            rankedJSON = toRankedJSON(list);
            WLLogger.log(Level.INFO, "EconomicFreedomIndex - refreshed (took " + (System.currentTimeMillis()-started) + "ms)");
            handler.handle(null);
        }
    }
}
