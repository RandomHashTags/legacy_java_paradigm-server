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

public enum PressFreedomIndex implements CountryRankingService {
    INSTANCE;

    private String rankedJSON;
    private HashMap<String, String> countries;

    @Override
    public CountryInfo getInfo() {
        return CountryInfo.RANKING_PRESS_FREEDOM_INDEX;
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
        final String url = "https://en.wikipedia.org/wiki/Press_Freedom_Index";
        final Elements trs = getDocumentElements(url, "div.mw-parser-output table.wikitable", 0).select("tbody tr");
        final EventSource wikipedia = new EventSource("Wikipedia: Press Freedom Index", url);
        final EventSources sources = new EventSources(wikipedia);

        trs.remove(0);
        trs.removeIf(element -> element.select("td").get(1).text().equals("N/A"));
        final int maxWorldRank = trs.size(), yearOfData = 2020;
        final Collection<CountryRankingInfoValue> list = new HashSet<>();
        int worldRank = 1;
        for(Element element : trs) {
            final Elements tds = element.select("td");
            final String country = tds.get(0).text().toLowerCase().replace(" ", "").replace(",", "").split("\\[")[0];
            final float score = Float.parseFloat(tds.get(1).text().split("\\)")[1]);
            final int defcon = score >= 70 ? 1 : score >= 50 ? 2 : score >= 35 ? 3 : score >= 15 ? 4 : 5;
            final CountryRankingInfoValue value = new CountryRankingInfoValue(defcon, country, maxWorldRank, worldRank, yearOfData, score, NumberType.FLOAT, false, "Press Freedom Index", " score", sources);
            list.add(value);
            countries.put(country, value.toString());
            worldRank += 1;
        }
        rankedJSON = toRankedJSON(list);
        handler.handle(null);
    }
}
