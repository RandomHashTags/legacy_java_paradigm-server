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

public enum HumanDevelopmentIndex implements CountryRankingService {
    INSTANCE;

    private String rankedJSON;
    private HashMap<String, String> countries;

    @Override
    public CountryInfo getInfo() {
        return CountryInfo.RANKING_HUMAN_DEVELOPMENT_INDEX;
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
        final String url = "https://en.wikipedia.org/wiki/List_of_countries_by_Human_Development_Index";
        final Elements trs = getRankingDocumentElements(url, "div.mw-parser-output table.wikitable", 0).select("tbody tr");
        final EventSource wikipedia = new EventSource("Wikipedia: List of countries by Human Development Index", url);
        final EventSources sources = new EventSources(wikipedia);

        trs.remove(0);
        trs.remove(0);
        trs.removeIf(element -> !element.select("th").isEmpty() || element.select("td").get(0).text().equals("—"));
        final int maxWorldRank = trs.size(), yearOfData = 2018;
        final Collection<CountryRankingInfoValue> list = new HashSet<>();
        for(Element element : trs) {
            final Elements tds = element.select("td");
            final int worldRank = Integer.parseInt(tds.get(0).text());
            final String country = tds.get(2).text().toLowerCase().split("\\[")[0].replace(" ", "");
            final float score = Float.parseFloat(tds.get(3).text());
            final int defcon = score < 0.450 ? 1 : score < 0.550 ? 2 : score < 0.70 ? 3 : score < 0.850 ? 4 : 5;
            final CountryRankingInfoValue value = new CountryRankingInfoValue(defcon, country, maxWorldRank, worldRank, yearOfData, score, NumberType.FLOAT, false, "Human Development Index", " HDI", sources);
            list.add(value);
            countries.put(country, value.toString());
        }
        rankedJSON = toRankedJSON(list);
        handler.handle(null);
    }
}
