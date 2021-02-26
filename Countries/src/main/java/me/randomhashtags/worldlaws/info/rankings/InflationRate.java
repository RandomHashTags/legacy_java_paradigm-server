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

public enum InflationRate implements CountryRankingService {
    INSTANCE;

    private String rankedJSON;
    private HashMap<String, String> countries;

    @Override
    public CountryInfo getInfo() {
        return CountryInfo.RANKING_INFLATION_RATE;
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
        final String url = "https://en.wikipedia.org/wiki/List_of_countries_by_inflation_rate";
        final Elements trs = getRankingDocumentElements(url, "div.mw-parser-output table.wikitable", 0).select("tbody tr");
        trs.remove(0);
        final List<CountryRankingInfoValue> list = new ArrayList<>();
        final EventSource source = new EventSource("Wikipedia: List of countries by inflation rate", url);
        final EventSources sources = new EventSources(source);
        final int maxWorldRank = trs.size();
        for(Element element : trs) {
            final Elements tds = element.select("td");
            final String country = tds.get(0).text().toLowerCase().replace(" ", "").replace(",", "");
            final String yearOfDataString = tds.get(2).text();
            final boolean isEstimate = yearOfDataString.contains(" est");
            final String yearOfDataValue = yearOfDataString.split(" est")[0].split(" \\[")[0].split("\\[")[0];
            final String[] yearOfDataStringArray = yearOfDataValue.split(" ");
            final int count = yearOfDataStringArray.length, yearOfData = Integer.parseInt(count == 1 ? yearOfDataValue : yearOfDataStringArray[0].matches("[0-9]+") ? yearOfDataStringArray[0] : yearOfDataStringArray[1]);
            String percentString = tds.get(1).text().replace(",", "");
            final boolean isPositive = percentString.split("\\.")[0].matches("[0-9]+");
            if(!isPositive) {
                percentString = percentString.substring(1);
            }
            final float percent = Float.parseFloat(percentString) * (isPositive ? 1 : -1);
            final int defcon = percent > 10.00 ? 1 : percent > 8.00 ? 2 : percent > 6.00 ? 3 : percent > 4.00 ? 4 : 5;
            final CountryRankingInfoValue value = new CountryRankingInfoValue(defcon, country, maxWorldRank, -1, yearOfData, percent, NumberType.FLOAT, isEstimate, "Inflation Rate", "%", sources);
            list.add(value);
        }
        list.sort(Comparator.comparing(inflationRateObj -> inflationRateObj.getValue().floatValue()));
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
