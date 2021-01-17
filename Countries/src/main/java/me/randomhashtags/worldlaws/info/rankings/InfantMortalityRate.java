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

public enum InfantMortalityRate implements CountryRankingService {
    INSTANCE;

    private String rankedJSON;
    private HashMap<String, String> countries;

    @Override
    public CountryInfo getInfo() {
        return CountryInfo.RANKING_INFANT_MORTALITY_RATE;
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
        final String url = "https://en.wikipedia.org/wiki/List_of_countries_by_infant_and_under-five_mortality_rates";
        final Elements trs = getDocumentElements(url, "div.mw-parser-output table tbody tr td table.wikitable", 1).select("tbody tr");
        trs.remove(0);
        final List<CountryRankingInfoValue> list = new ArrayList<>();
        final EventSource source = new EventSource("Wikipedia: List of countries by infant and under-five mortality rates", url);
        final EventSources sources = new EventSources(source);
        final int maxWorldRank = trs.size(), yearOfData = 2019;
        for(Element element : trs) {
            final Elements tds = element.select("td");
            final String country = tds.get(0).text().toLowerCase().split("\\(")[0].replace(" ", "").replace(",", "");
            final float mortalityRate = Float.parseFloat(tds.get(1).text());
            final int defcon = mortalityRate >= 80.00 ? 1 : mortalityRate >= 60.00 ? 2 : mortalityRate >= 40.00 ? 3 : mortalityRate >= 20.00 ? 4 : 5;
            final CountryRankingInfoValue value = new CountryRankingInfoValue(defcon, country, maxWorldRank, -1, yearOfData, mortalityRate, NumberType.FLOAT, true, "Under-5 mortality rate", " per 1,000 live births", sources);
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
