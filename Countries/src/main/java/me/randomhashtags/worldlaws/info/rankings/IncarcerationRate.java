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

public enum IncarcerationRate implements CountryRankingService {
    INSTANCE;

    private String rankedJSON;
    private HashMap<String, String> countries;

    @Override
    public CountryInfo getInfo() {
        return CountryInfo.RANKING_INCARCERATION_RATE;
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
        final String url = "https://en.wikipedia.org/wiki/List_of_countries_by_incarceration_rate";
        final Elements trs = getDocumentElements(url, "div.mw-parser-output table tbody tr td table.wikitable", 1).select("tbody tr");
        trs.remove(0);
        final List<CountryRankingInfoValue> list = new ArrayList<>();
        final EventSource source = new EventSource("Wikipedia: List of countries by incarceration rate", url);
        final EventSources sources = new EventSources(source);
        final int maxWorldRank = trs.size(), yearOfData = 2020;
        for(Element element : trs) {
            final Elements tds = element.select("td");
            final String country = tds.get(0).text().toLowerCase().replace(" ", "").split("ofamerica")[0].split("nfederation")[0];
            final int ratePer100_000 = Integer.parseInt(tds.get(1).text()), prisonPopulation = Integer.parseInt(tds.get(2).text().replace(",", ""));
            final int defcon = ratePer100_000 > 500 ? 1 : ratePer100_000 > 350 ? 2 : ratePer100_000 > 250 ? 3 : ratePer100_000 > 150 ? 4 : 5;
            final CountryRankingInfoValue value = new CountryRankingInfoValue(defcon, country, maxWorldRank, -1, yearOfData, ratePer100_000, NumberType.INTEGER, false, "Incarceration Rate", " per 100,000", sources);
            final List<CountryRankingInfoValueOther> values = new ArrayList<>();
            values.add(new CountryRankingInfoValueOther(prisonPopulation, NumberType.INTEGER, "Prison Population", " people"));
            value.setOtherValues(values);
            list.add(value);
        }
        list.sort(Comparator.comparingInt(incarRate -> incarRate.getValue().intValue()));
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
