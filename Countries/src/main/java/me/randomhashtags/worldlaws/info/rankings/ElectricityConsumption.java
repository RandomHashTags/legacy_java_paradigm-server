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

public enum ElectricityConsumption implements CountryRankingService {
    INSTANCE;

    private String rankedJSON;
    private HashMap<String, String> countries;

    @Override
    public CountryInfo getInfo() {
        return CountryInfo.RANKING_ELECTRICITY_CONSUMPTION;
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
        final String url = "https://en.wikipedia.org/wiki/List_of_countries_by_electricity_consumption";
        final Elements elements = getDocumentElements(url, "div.mw-content-ltr div.mw-parser-output table.wikitable", 0).select("tbody tr");
        elements.remove(0);
        elements.remove(0);
        final int maxWorldRank = elements.size();
        final EventSource wikipedia = new EventSource("Wikipedia: List of countries by electricity consumption", url);
        final EventSources sources = new EventSources(wikipedia);
        final Collection<CountryRankingInfoValue> list = new HashSet<>();
        for(Element element : elements) {
            final Elements tds = element.select("td");
            final int worldRank = Integer.parseInt(tds.get(0).text());
            final String country = tds.get(1).text().toLowerCase().replace(" ", "");
            final long consumption = Long.parseLong(tds.get(2).text().split("\\[")[0].replace(",", "").split("\\.")[0]);
            final String[] yearValues = tds.get(3).text().split(" ");
            final int yearOfData = Integer.parseInt(yearValues[0]);
            final boolean isEstimate = yearValues.length > 1;
            final int defcon = -1;
            final CountryRankingInfoValue value = new CountryRankingInfoValue(defcon, country, maxWorldRank, worldRank, yearOfData, consumption, NumberType.LONG, isEstimate, "Electricity Consumption", " kW*h/yr", sources);
            list.add(value);
            countries.put(country, value.toString());
        }
        rankedJSON = toRankedJSON(list);
        handler.handle(null);
    }
}
