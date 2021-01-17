package me.randomhashtags.worldlaws.info.rankings;

import me.randomhashtags.worldlaws.CompletionHandler;
import me.randomhashtags.worldlaws.EventSource;
import me.randomhashtags.worldlaws.EventSources;
import me.randomhashtags.worldlaws.NumberType;
import me.randomhashtags.worldlaws.location.CountryInfo;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;

public enum WorldGivingIndex implements CountryRankingService {
    INSTANCE;

    private String rankedJSON;
    private HashMap<String, String> countries;

    @Override
    public CountryInfo getInfo() {
        return CountryInfo.RANKING_WORLD_GIVING_INDEX;
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
        final String url = "https://en.wikipedia.org/wiki/World_Giving_Index";
        final Elements trs = getDocumentElements(url, "div.mw-parser-output table.wikitable", 0).select("tbody tr");
        final int yearOfData = Integer.parseInt(trs.get(0).select("th").get(1).text().split(" ")[0]);
        trs.remove(0);
        trs.removeIf(row -> {
            final String text = row.select("td").get(1).text();
            return text.equals("n/a");
        });
        final EventSource wikipedia = new EventSource("Wikipedia: World Giving Index", url);
        final EventSources sources = new EventSources(wikipedia);
        final int maxWorldRank = trs.size();
        final Collection<CountryRankingInfoValue> list = new HashSet<>();
        for(Element element : trs) {
            final Elements tds = element.select("td");
            final String country = tds.get(0).select("a").get(0).text().toLowerCase().replace(" ", "");
            final int worldRank = Integer.parseInt(tds.get(1).text());
            final int defcon = worldRank >= 100 ? 1 : worldRank >= 80 ? 2 : worldRank >= 60 ? 3 : worldRank >= 40 ? 4 : 5;
            final CountryRankingInfoValue value = new CountryRankingInfoValue(defcon, country, maxWorldRank, worldRank, yearOfData, -1, NumberType.FLOAT, false, "World Giving Index", null, sources);
            list.add(value);
            countries.put(country, value.toString());
        }
        rankedJSON = toRankedJSON(list);
        handler.handle(null);
    }
}
