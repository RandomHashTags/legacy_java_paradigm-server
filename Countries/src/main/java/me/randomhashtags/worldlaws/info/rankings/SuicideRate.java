package me.randomhashtags.worldlaws.info.rankings;

import me.randomhashtags.worldlaws.CompletionHandler;
import me.randomhashtags.worldlaws.EventSource;
import me.randomhashtags.worldlaws.EventSources;
import me.randomhashtags.worldlaws.NumberType;
import me.randomhashtags.worldlaws.location.CountryInfo;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.*;

public enum SuicideRate implements CountryRankingService {
    INSTANCE;

    private String rankedJSON;
    private HashMap<String, String> countries;

    @Override
    public CountryInfo getInfo() {
        return CountryInfo.RANKING_SUICIDE_RATE;
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
        final String url = "https://en.wikipedia.org/wiki/List_of_countries_by_suicide_rate";
        final Elements trs = getRankingDocumentElements(url, "div.mw-parser-output table.wikitable", 2).select("tbody tr");
        final EventSource wikipedia = new EventSource("Wikipedia: List of countries by suicide rate", url);
        final EventSources sources = new EventSources(wikipedia);

        trs.remove(0);
        trs.remove(0);
        final int maxWorldRank = trs.size(), yearOfData = 2016;
        final Collection<CountryRankingInfoValue> list = new HashSet<>();
        for(Element element : trs) {
            final Elements tds = element.select("td");
            final int worldRank = Integer.parseInt(tds.get(0).text()), maleWorldRank = Integer.parseInt(tds.get(4).text()), femaleWorldRank = Integer.parseInt(tds.get(6).text());
            final String country = tds.get(1).text().toLowerCase().split("\\[")[0].split("\\(")[0].replace(" ", "");
            final float ratePer100_000 = Float.parseFloat(tds.get(3).text());
            final float ratePer100_000male = Float.parseFloat(tds.get(5).text());
            final float ratePer100_000female = Float.parseFloat(tds.get(7).text());
            final int defcon = ratePer100_000 > 20.00 ? 1 : ratePer100_000 > 15.00 ? 2 : ratePer100_000 > 10.00 ? 3 : ratePer100_000 > 5.00 ? 4 : 5;
            final CountryRankingInfoValue value = new CountryRankingInfoValue(defcon, country, maxWorldRank, worldRank, yearOfData, ratePer100_000, NumberType.FLOAT, true, "Suicide Rate", " per 100,000", sources);
            final List<CountryRankingInfoValueOther> values = new ArrayList<>();
            values.add(new CountryRankingInfoValueOther(maleWorldRank, NumberType.INTEGER, "Male Suicide World Rank", null));
            values.add(new CountryRankingInfoValueOther(ratePer100_000male, NumberType.FLOAT, "Male Suicide Rate", " per 100,000"));
            values.add(new CountryRankingInfoValueOther(femaleWorldRank, NumberType.INTEGER, "Female Suicide World Rank", null));
            values.add(new CountryRankingInfoValueOther(ratePer100_000female, NumberType.FLOAT, "Female Suicide Rate", " per 100,000"));
            value.setOtherValues(values);
            list.add(value);
            countries.put(country, value.toString());
        }
        rankedJSON = toRankedJSON(list);
        handler.handle(null);
    }
}
