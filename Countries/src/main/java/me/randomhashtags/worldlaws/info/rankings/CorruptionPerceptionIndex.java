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

public enum CorruptionPerceptionIndex implements CountryRankingService {
    INSTANCE;

    private String rankedJSON;
    private HashMap<String, String> countries;

    @Override
    public CountryInfo getInfo() {
        return CountryInfo.RANKING_CORRUPTION_PERCEPTION_INDEX;
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
            WLLogger.log(Level.WARNING,"CorruptionPerceptionsIndex - missing value for country \"" + countryBackendID + "\"!");
        }
        return value;
    }

    private void refresh(CompletionHandler handler) {
        final long started = System.currentTimeMillis();
        countries = new HashMap<>();
        final String url = "https://en.wikipedia.org/wiki/Corruption_Perceptions_Index";
        final Document doc = getDocument(url);
        if(doc != null) {
            final EventSource wikipedia = new EventSource("Wikipedia: Corruption Perceptions Index", url);
            final EventSources sources = new EventSources(wikipedia);

            final Elements trs = doc.select("div.mw-parser-output table.wikitable").get(1).select("tbody tr");
            trs.remove(0);
            trs.remove(0);
            final int maxWorldRank = trs.size(), yearOfData = 2019;
            final Collection<CountryRankingInfoValue> list = new HashSet<>();
            for(Element element : trs) {
                final Elements tds = element.select("td");
                final int worldRank = Integer.parseInt(tds.get(0).text());
                final String country = tds.get(1).select("span.datasortkey a").get(0).text().toLowerCase().replace(" ", "");
                final float score = Float.parseFloat(tds.get(2).text());
                final int defcon = score < 40 ? 1 : score < 60 ? 2 : score < 75 ? 3 : score < 90 ? 4 : 5;
                final CountryRankingInfoValue value = new CountryRankingInfoValue(defcon, country, maxWorldRank, worldRank, yearOfData, score, NumberType.FLOAT, false, "Corruption Perceptions Index", " score", sources);
                list.add(value);
                countries.put(country, value.toString());
            }
            rankedJSON = toRankedJSON(list);
            WLLogger.log(Level.INFO, "CorruptionPerceptionsIndex - refreshed " + countries.size() + " nations/territories (took " + (System.currentTimeMillis()-started) + "ms)");
            handler.handle(null);
        }
    }
}
