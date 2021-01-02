package me.randomhashtags.worldlaws.info.rankings;

import me.randomhashtags.worldlaws.*;
import me.randomhashtags.worldlaws.info.rankings.CountryRankingInfoValue;
import me.randomhashtags.worldlaws.info.rankings.CountryRankingInfoValueOther;
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

public enum FreedomRankings implements CountryRankingService {
    INSTANCE;

    private String rankedJSON;
    private HashMap<String, String> countries;

    @Override
    public CountryInfo getInfo() {
        return CountryInfo.RANKING_FREEDOM_RANKINGS;
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
            WLLogger.log(Level.WARNING, "FreedomRankings - missing for country \"" + countryBackendID + "\"!");
        }
        return value;
    }

    private void refresh(CompletionHandler handler) {
        final long started = System.currentTimeMillis();
        countries = new HashMap<>();
        final String url = "https://en.wikipedia.org/wiki/Freedom_in_the_World";
        final Document doc = getDocument(url);
        if(doc != null) {
            final Elements trs = doc.select("div.mw-parser-output table.wikitable").get(0).select("tbody tr");
            trs.remove(0);
            trs.remove(0);
            final List<CountryRankingInfoValue> list = new ArrayList<>();
            final int maxWorldRank = trs.size(), yearOfData = 2020;
            final EventSources sources = new EventSources(new EventSource("Wikipedia: Freedom in the World", url));
            for(Element element : trs) {
                final Elements tds = element.select("td");
                final String country = tds.get(0).text().toLowerCase().replace(" ", "").replace("*", "");
                final int max = tds.size(), points = Integer.parseInt(tds.get(max-1).text()), civilRightsScore = Integer.parseInt(tds.get(max-3).text()), politicalRightsScore = Integer.parseInt(tds.get(max-4).text());
                final int defcon = points <= 30 ? 1 : points <= 35 ? 2 : points <= 60 ? 3 : points <= 70 ? 4 : 5;
                final CountryRankingInfoValue value = new CountryRankingInfoValue(defcon, country, maxWorldRank, -1, yearOfData, points, NumberType.INTEGER, false, "Freedom Rankings", "pts", sources);
                final List<CountryRankingInfoValueOther> values = new ArrayList<>();
                values.add(new CountryRankingInfoValueOther(politicalRightsScore, NumberType.INTEGER, "Political Rights Score", "pts"));
                values.add(new CountryRankingInfoValueOther(civilRightsScore, NumberType.INTEGER, "Civil Rights Score", "pts"));
                value.setOtherValues(values);
                list.add(value);
            }
            list.sort(Comparator.comparingInt(freedomRank -> freedomRank.getValue().intValue()));
            int worldRank = maxWorldRank;
            for(CountryRankingInfoValue rank : list) {
                rank.worldRank = worldRank;
                countries.put(rank.country, rank.toString());
                worldRank -= 1;
            }
            rankedJSON = toRankedJSON(list);
            WLLogger.log(Level.INFO, "FreedomRankings - refreshed (took " + (System.currentTimeMillis()-started) + "ms)");
            handler.handle(null);
        }
    }
}
