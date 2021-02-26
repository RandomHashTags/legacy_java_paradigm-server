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

public enum AdultHIVRate implements CountryRankingService {
    INSTANCE;

    private String rankedJSON;
    private HashMap<String, String> countries;

    @Override
    public CountryInfo getInfo() {
        return CountryInfo.RANKING_ADULT_HIV_PREVALENCE;
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
        final String url = "https://en.wikipedia.org/wiki/List_of_countries_by_HIV/AIDS_adult_prevalence_rate";
        final Elements trs = getRankingDocumentElements(url, "div.mw-parser-output table.wikitable", 0).select("tbody tr");
        trs.remove(0);
        final List<CountryRankingInfoValue> list = new ArrayList<>();
        final EventSource source = new EventSource("Wikipedia: List of countries by HIV/AIDS adult prevalence rate", url);
        final EventSources sources = new EventSources(source);
        final int maxWorldRank = trs.size();
        for(Element element : trs) {
            final Elements tds = element.select("td");
            final String country = tds.get(0).text().toLowerCase().split("\\[")[0].split("\\(")[0].replace(" ", "").replace(",", "");
            final int yearOfData = Integer.parseInt(tds.get(4).text().split("\\[")[0]);
            final String percentString = tds.get(1).text();
            final float percent = percentString.equals("-") ? -1 : Float.parseFloat(percentString.split("%")[0]);
            final int defcon = percent >= 10.00 ? 1 : percent >= 8.00 ? 2 : percent >= 6.00 ? 3 : percent >= 4.00 ? 4 : 5;
            final CountryRankingInfoValue value = new CountryRankingInfoValue(defcon, country, maxWorldRank, -1, yearOfData, percent, NumberType.FLOAT, true, "Adult HIV prevalence", "%", sources);
            list.add(value);
        }
        list.sort(Comparator.comparing(rate -> rate.getValue().floatValue()));
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
