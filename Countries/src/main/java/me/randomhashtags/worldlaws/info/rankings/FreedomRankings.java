package me.randomhashtags.worldlaws.info.rankings;

import me.randomhashtags.worldlaws.CompletionHandler;
import me.randomhashtags.worldlaws.EventSource;
import me.randomhashtags.worldlaws.EventSources;
import me.randomhashtags.worldlaws.NumberType;
import me.randomhashtags.worldlaws.location.CountryInfo;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

public enum FreedomRankings implements CountryRankingService {
    INSTANCE;

    private String rankedJSON;
    private HashMap<String, String> countries;

    @Override
    public CountryInfo getInfo() {
        return CountryInfo.RANKING_FREEDOM_RANKINGS;
    }

    @Override
    public HashMap<String, String> getCountries() {
        return countries;
    }

    @Override
    public String getRankedJSON() {
        return rankedJSON;
    }

    @Override
    public void refresh(CompletionHandler handler) {
        countries = new HashMap<>();
        final String url = "https://en.wikipedia.org/wiki/Freedom_in_the_World";
        final Elements trs = getDocumentElements(url, "div.mw-parser-output table.wikitable", 0).select("tbody tr");
        trs.remove(0);
        trs.remove(0);
        final List<CountryRankingInfoValue> list = new ArrayList<>();
        final int maxWorldRank = trs.size(), yearOfData = 2020;
        final EventSource source = new EventSource("Wikipedia: Freedom in the World", url);
        final EventSources sources = new EventSources(source);
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
        handler.handle(null);
    }
}
