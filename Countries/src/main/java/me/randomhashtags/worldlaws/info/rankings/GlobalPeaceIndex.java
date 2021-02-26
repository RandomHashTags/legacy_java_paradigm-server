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

public enum GlobalPeaceIndex implements CountryRankingService {
    INSTANCE;

    private String rankedJSON;
    private HashMap<String, String> countries;

    @Override
    public CountryInfo getInfo() {
        return CountryInfo.RANKING_GLOBAL_PEACE_INDEX;
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
        final String url = "https://en.wikipedia.org/wiki/Global_Peace_Index";
        final Elements trs = getRankingDocumentElements(url, "div.mw-parser-output table.wikitable", 1).select("tbody tr");
        final EventSource wikipedia = new EventSource("Wikipedia: Global Peace Index", url);
        final EventSources sources = new EventSources(wikipedia);

        trs.remove(0);
        final int maxWorldRank = trs.size(), yearOfData = 2019;
        final Collection<CountryRankingInfoValue> list = new HashSet<>();
        for(Element element : trs) {
            final Elements tds = element.select("td");
            final int worldRank = Integer.parseInt(tds.get(1).text().replace("=", ""));
            final String country = tds.get(0).select("a").get(0).text().toLowerCase().replace(" ", "");
            final float score = Float.parseFloat(tds.get(2).text());
            final int defcon = score >= 2.75 ? 1 : score >= 2.50 ? 2 : score >= 2.25 ? 3 : score >= 2.00 ? 4 : 5;
            final CountryRankingInfoValue value = new CountryRankingInfoValue(defcon, country, maxWorldRank, worldRank, yearOfData, score, NumberType.FLOAT, false, "Global Peace Index", " score", sources);
            list.add(value);
            countries.put(country, value.toString());
        }
        rankedJSON = toRankedJSON(list);
        handler.handle(null);
    }
}
