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

public enum ObesityRate implements CountryRankingService {
    INSTANCE;

    private String rankedJSON;
    private HashMap<String, String> countries;

    @Override
    public CountryInfo getInfo() {
        return CountryInfo.RANKING_OBESITY_RATE;
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
        final String url = "https://en.wikipedia.org/wiki/List_of_countries_by_obesity_rate";
        final Elements elements = getRankingDocumentElements(url, "div.mw-parser-output table.wikitable", 0).select("tbody tr");
        elements.remove(0);
        final int maxWorldRank = elements.size(), yearOfData = 2016;
        final EventSource source = new EventSource("Wikipedia: List of countries by obesity rate", url);
        final EventSources sources = new EventSources(source);
        final Collection<CountryRankingInfoValue> list = new HashSet<>();
        for(Element element : elements) {
            final Elements tds = element.select("td");
            final String country = tds.get(0).text().toLowerCase().replace(" ", "");
            final int worldRank = Integer.parseInt(tds.get(1).text());
            final float percent = Float.parseFloat(tds.get(2).text());
            final int defcon = percent >= 50.00 ? 1 : percent >= 40.00 ? 2 : percent >= 30.00 ? 3 : percent >= 20.00 ? 4 : 5;
            final CountryRankingInfoValue value = new CountryRankingInfoValue(defcon, country, maxWorldRank, worldRank, yearOfData, percent, NumberType.FLOAT, false, "Obesity Rate", "%", sources);
            list.add(value);
            countries.put(country, value.toString());
        }
        rankedJSON = toRankedJSON(list);
        handler.handle(null);
    }
}
