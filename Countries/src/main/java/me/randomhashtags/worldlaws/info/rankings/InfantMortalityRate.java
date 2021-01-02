package me.randomhashtags.worldlaws.info.rankings;

import me.randomhashtags.worldlaws.*;
import me.randomhashtags.worldlaws.info.rankings.CountryRankingInfoValue;
import me.randomhashtags.worldlaws.info.rankings.CountryRankingService;
import me.randomhashtags.worldlaws.location.CountryInfo;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;

// https://en.wikipedia.org/wiki/List_of_countries_by_infant_and_under-five_mortality_rates
public enum InfantMortalityRate implements CountryRankingService {
    INSTANCE;

    private String rankedJSON;
    private HashMap<String, String> countries;

    @Override
    public CountryInfo getInfo() {
        return CountryInfo.RANKING_INFANT_MORTALITY_RATE;
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
            WLLogger.log(Level.WARNING, "InfantMortalityRate - missing for country \"" + countryBackendID + "\"!");
        }
        return value;
    }

    private void refresh(CompletionHandler handler) {
        final long started = System.currentTimeMillis();
        countries = new HashMap<>();
        final String url = "https://en.wikipedia.org/wiki/List_of_countries_by_infant_and_under-five_mortality_rates";
        final Document doc = getDocument(url);
        if(doc != null) {
            final Elements trs = doc.select("div.mw-parser-output table tbody tr td table.wikitable").get(1).select("tbody tr");
            trs.remove(0);
            final List<CountryRankingInfoValue> list = new ArrayList<>();
            final EventSources sources = new EventSources(new EventSource("Wikipedia: List of countries by infant and under-five mortality rates", url));
            final int maxWorldRank = trs.size(), yearOfData = 2019;
            for(Element element : trs) {
                final Elements tds = element.select("td");
                final String country = tds.get(0).text().toLowerCase().split("\\(")[0].replace(" ", "").replace(",", "");
                final float mortalityRate = Float.parseFloat(tds.get(1).text());
                final int defcon = mortalityRate >= 80.00 ? 1 : mortalityRate >= 60.00 ? 2 : mortalityRate >= 40.00 ? 3 : mortalityRate >= 20.00 ? 4 : 5;
                final CountryRankingInfoValue value = new CountryRankingInfoValue(defcon, country, maxWorldRank, -1, yearOfData, mortalityRate, NumberType.FLOAT, true, "Under-5 mortality rate", " per 1,000 live births", sources);
                list.add(value);
            }
            list.sort(Comparator.comparing(rate -> rate.getValue().floatValue()));
            int worldRank = maxWorldRank;
            for(CountryRankingInfoValue rank : list) {
                rank.worldRank = worldRank;
                countries.put(rank.country, rank.toString());
                worldRank -= 1;
            }
            rankedJSON = toRankedJSON(list);
            WLLogger.log(Level.INFO, "InfantMortalityRate - refreshed (took " + (System.currentTimeMillis()-started) + "ms)");
            handler.handle(null);
        }
    }
}
