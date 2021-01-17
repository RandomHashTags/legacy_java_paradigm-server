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

public enum NaturalDisasterRisk implements CountryRankingService {
    INSTANCE;

    private String rankedJSON;
    private HashMap<String, String> countries;

    @Override
    public CountryInfo getInfo() {
        return CountryInfo.RANKING_NATURAL_DISASTER_RISK;
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
        final String url = "https://en.wikipedia.org/wiki/List_of_countries_by_natural_disaster_risk";
        final Elements trs = getDocumentElements(url, "div.mw-parser-output table.wikitable", 0).select("tbody tr");
        final EventSource wikipedia = new EventSource("Wikipedia: List of countries by natural disaster risk", url);
        final EventSources sources = new EventSources(wikipedia);

        trs.remove(0);
        trs.removeIf(element -> element.select("td").get(2).text().equals("—"));
        final int maxWorldRank = trs.size(), yearOfData = 2017;
        final List<CountryRankingInfoValue> list = new ArrayList<>();
        for(Element element : trs) {
            final Elements tds = element.select("td");
            final String country = tds.get(1).text().toLowerCase().replace(" ", "");
            final float score = Float.parseFloat(tds.get(2).text().replace("%", ""));
            final int defcon = score >= 10.30 ? 1 : score >= 7.10 ? 2 : score >= 5.50 ? 3 : score >= 3.40 ? 4 : 5;
            final CountryRankingInfoValue value = new CountryRankingInfoValue(defcon, country, maxWorldRank, -1, yearOfData, score, NumberType.FLOAT, false, "Natural Disaster Risk", "% risk", sources);
            list.add(value);
            countries.put(country, value.toString());
        }
        list.sort(Comparator.comparingDouble(country -> ((Float) country.getValue())));
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
