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

public enum CannabisUse implements CountryRankingService {
    INSTANCE;

    private String rankedJSON;
    private HashMap<String, String> countries;

    @Override
    public CountryInfo getInfo() {
        return CountryInfo.RANKING_CANNABIS_USE;
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
            WLLogger.log(Level.WARNING, "CannabisUse - missing for country \"" + countryBackendID + "\"!");
        }
        return value;
    }

    private void refresh(CompletionHandler handler) {
        final long started = System.currentTimeMillis();
        countries = new HashMap<>();
        final String url = "https://en.wikipedia.org/wiki/Annual_cannabis_use_by_country";
        final Document doc = getDocument(url);
        if(doc != null) {
            final Elements trs = doc.select("div.mw-parser-output table.wikitable").get(0).select("tbody tr");
            trs.remove(0);
            final List<CountryRankingInfoValue> list = new ArrayList<>();
            final EventSources sources = new EventSources(new EventSource("Wikipedia: Annual cannabis use by country", url));
            final int maxWorldRank = trs.size();
            for(Element element : trs) {
                final Elements tds = element.select("td");
                final String country = tds.get(0).text().toLowerCase().replace(" ", "");
                final String yearOfDataString = tds.get(2).text();
                final int yearOfData = yearOfDataString.isEmpty() ? -1 : Integer.parseInt(yearOfDataString);
                final float percent = Float.parseFloat(tds.get(1).text());
                final int defcon = percent >= 15.00 ? 1 : percent >= 12.00 ? 2 : percent >= 9.00 ? 3 : percent >= 6.00 ? 4 : 5;
                final CountryRankingInfoValue value = new CountryRankingInfoValue(defcon, country, maxWorldRank, -1, yearOfData, percent, NumberType.FLOAT, false, "Cannabis Use", "%", sources);
                list.add(value);
            }
            list.sort(Comparator.comparing(cannabisUseObj -> cannabisUseObj.getValue().floatValue()));
            int worldRank = maxWorldRank;
            for(CountryRankingInfoValue rank : list) {
                rank.worldRank = worldRank;
                countries.put(rank.country, rank.toString());
                worldRank -= 1;
            }
            rankedJSON = toRankedJSON(list);
            WLLogger.log(Level.INFO, "CannabisUse - refreshed (took " + (System.currentTimeMillis()-started) + "ms)");
            handler.handle(null);
        }
    }
}
