package me.randomhashtags.worldlaws.info.rankings;

import me.randomhashtags.worldlaws.*;
import me.randomhashtags.worldlaws.location.CountryInfo;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.*;
import java.util.logging.Level;

public enum GlobalTerrorismIndex implements CountryRankingService {
    INSTANCE;

    private String rankedJSON;
    private HashMap<String, String> countries;

    @Override
    public CountryInfo getInfo() {
        return CountryInfo.RANKING_GLOBAL_TERRORISM_INDEX;
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
            WLLogger.log(Level.WARNING, "GlobalTerrorismIndex - missing for country \"" + countryBackendID + "\"!");
        }
        return value;
    }

    private void refresh(CompletionHandler handler) {
        final long started = System.currentTimeMillis();
        countries = new HashMap<>();
        final String url = "https://en.wikipedia.org/wiki/Global_Terrorism_Index";
        final Document doc = getDocument(url);
        if(doc != null) {
            final Elements elements = doc.select("div.mw-parser-output table.wikitable").get(3).select("tbody tr");
            elements.remove(0);
            final EventSource wikipedia = new EventSource("Wikipedia: Global Terrorism Index", url);
            final EventSources sources = new EventSources(wikipedia);
            final int maxWorldRank = elements.size(), yearOfData = 2020;
            final Collection<CountryRankingInfoValue> list = new HashSet<>();
            for(Element element : elements) {
                final Elements tds = element.select("td");
                final int worldRank = Integer.parseInt(tds.get(0).text().replace("=", ""));
                final String country = tds.get(1).text().toLowerCase().replace(" ", "").replace(",", "").replace("people'srepublicof", "");
                final float score = Float.parseFloat(tds.get(2).text());
                final int defcon = score >= 8 ? 1 : score >= 6 ? 2 : score >= 4 ? 3 : score >= 2 ? 4 : 5;
                final CountryRankingInfoValue value = new CountryRankingInfoValue(defcon, country, maxWorldRank, worldRank, yearOfData, score, NumberType.FLOAT, false, "Global Terrorism Index", " score", sources);
                list.add(value);
                countries.put(country, value.toString());
            }
            rankedJSON = toRankedJSON(list);
            WLLogger.log(Level.INFO, "GlobalTerrorismIndex - refreshed (took " + (System.currentTimeMillis()-started) + "ms)");
            handler.handle(null);
        }
    }
}
