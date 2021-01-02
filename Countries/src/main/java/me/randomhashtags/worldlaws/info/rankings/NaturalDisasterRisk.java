package me.randomhashtags.worldlaws.info.rankings;

import me.randomhashtags.worldlaws.*;
import me.randomhashtags.worldlaws.info.rankings.CountryRankingInfoValue;
import me.randomhashtags.worldlaws.info.rankings.CountryRankingService;
import me.randomhashtags.worldlaws.location.CountryInfo;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.*;
import java.util.logging.Level;

public enum NaturalDisasterRisk implements CountryRankingService {
    INSTANCE;

    private String rankedJSON;
    private HashMap<String, String> countries;

    @Override
    public CountryInfo getInfo() {
        return CountryInfo.RANKING_NATURAL_DISASTER_RISK;
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
            WLLogger.log(Level.WARNING,"NaturalDisasterRisk - missing value for country \"" + countryBackendID + "\"!");
        }
        return value;
    }

    private void refresh(CompletionHandler handler) {
        final long started = System.currentTimeMillis();
        countries = new HashMap<>();
        final String url = "https://en.wikipedia.org/wiki/List_of_countries_by_natural_disaster_risk";
        final Document doc = getDocument(url);
        if(doc != null) {
            final EventSource wikipedia = new EventSource("Wikipedia: List of countries by natural disaster risk", url);
            final EventSources sources = new EventSources(wikipedia);

            final Elements trs = doc.select("div.mw-parser-output table.wikitable").get(0).select("tbody tr");
            trs.remove(0);
            trs.removeIf(element -> element.select("td").get(2).text().equals("—"));
            final int maxWorldRank = trs.size(), yearOfData = 2017;
            final List<CountryRankingInfoValue> list = new ArrayList<>();
            for(Element element : trs) {
                final Elements tds = element.select("td");
                final String country = tds.get(1).text().toLowerCase().replace(" ", "");
                final float score = Float.parseFloat(tds.get(2).text().replace("%", ""));
                final int defcon = score >= 10.30 ? 1 : score >= 7.10 ? 2 : score >= 5.50 ? 3 : score >= 3.40 ? 4 : 5;
                final CountryRankingInfoValue value = new CountryRankingInfoValue(defcon, country, maxWorldRank, -1, yearOfData, score, NumberType.FLOAT, false, "Natural Disaster Risk", "% risk", sources);
                list.add(value);
                countries.put(country, value.toString());
            }
            list.sort(Comparator.comparingDouble(country -> ((Float) country.getValue())));
            int worldRank = maxWorldRank;
            for(CountryRankingInfoValue rank : list) {
                rank.worldRank = worldRank;
                countries.put(rank.country, rank.toString());
                worldRank -= 1;
            }
            rankedJSON = toRankedJSON(list);
            WLLogger.log(Level.INFO, "NaturalDisasterRisk - refreshed (took " + (System.currentTimeMillis()-started) + "ms)");
            handler.handle(null);
        }
    }
}
