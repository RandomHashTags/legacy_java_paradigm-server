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

public enum CannabisUse implements CountryRankingService {
    INSTANCE;

    private String rankedJSON;
    private HashMap<String, String> countries;

    @Override
    public CountryInfo getInfo() {
        return CountryInfo.RANKING_CANNABIS_USE;
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
        final String url = "https://en.wikipedia.org/wiki/Annual_cannabis_use_by_country";
        final Elements trs = getDocumentElements(url, "div.mw-parser-output table.wikitable", 0).select("tbody tr");
        trs.remove(0);
        final List<CountryRankingInfoValue> list = new ArrayList<>();
        final EventSource source = new EventSource("Wikipedia: Annual cannabis use by country", url);
        final EventSources sources = new EventSources(source);
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
        handler.handle(null);
    }
}
