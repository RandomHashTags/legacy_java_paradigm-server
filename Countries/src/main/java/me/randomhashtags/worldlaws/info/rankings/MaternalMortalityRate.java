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

public enum MaternalMortalityRate implements CountryRankingService {
    INSTANCE;

    private String rankedJSON;
    private HashMap<String, String> countries;

    @Override
    public CountryInfo getInfo() {
        return CountryInfo.RANKING_MATERNAL_MORTALITY_RATE;
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
        final String url = "https://en.wikipedia.org/wiki/List_of_countries_by_maternal_mortality_ratio";
        final Elements elements = getDocumentElements(url, "div.mw-parser-output table.wikitable", 0).select("tbody tr");
        elements.remove(0);
        elements.remove(elements.size()-1);
        final EventSource wikipedia = new EventSource("Wikipedia: List of countries by maternal mortality ratio", url);
        final EventSources sources = new EventSources(wikipedia);
        final int maxWorldRank = elements.size(), yearOfData = 2017;
        final Collection<CountryRankingInfoValue> list = new HashSet<>();
        int previousWorldRank = -1;
        for(Element element : elements) {
            final Elements tds = element.select("td");
            final boolean hasWorldRank = tds.size() == 3;
            final String country = tds.get(hasWorldRank ? 1 : 0).text().toLowerCase().split("\\(")[0].replace(" ", "");
            final int worldRank = hasWorldRank ? Integer.parseInt(tds.get(0).text()) : previousWorldRank;
            previousWorldRank = worldRank;
            final int ratio = Integer.parseInt(tds.get(hasWorldRank ? 2 : 1).text().split("\\[")[0].replace(",", ""));
            final int defcon = ratio >= 350 ? 1 : ratio >= 280 ? 2 : ratio >= 140 ? 3 : ratio >= 70 ? 4 : 5;
            final CountryRankingInfoValue value = new CountryRankingInfoValue(defcon, country, maxWorldRank, worldRank, yearOfData, ratio, NumberType.INTEGER, false, "Maternal Mortality Rate", " per 100,000 live births", sources);
            list.add(value);
            countries.put(country, value.toString());
        }
        rankedJSON = toRankedJSON(list);
        handler.handle(null);
    }
}
