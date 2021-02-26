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

public enum QualityOfNationalityIndex implements CountryRankingService {
    INSTANCE;

    private String rankedJSON;
    private HashMap<String, String> countries;

    @Override
    public CountryInfo getInfo() {
        return CountryInfo.RANKING_QUALITY_OF_NATIONALITY_INDEX;
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
        final String url = "https://en.wikipedia.org/wiki/The_Quality_of_Nationality_Index";
        final Elements elements = getRankingDocumentElements(url, "div.mw-parser-output table.wikitable", 0).select("tbody tr");
        elements.remove(0);
        final EventSource wikipedia = new EventSource("Wikipedia: The Quality of Nationality Index", url);
        final EventSources sources = new EventSources(wikipedia);
        final int maxWorldRank = elements.size(), yearOfData = 2018;
        final Collection<CountryRankingInfoValue> list = new HashSet<>();
        for(Element element : elements) {
            final Elements tds = element.select("td");
            final int worldRank = Integer.parseInt(tds.get(0).text());
            final String country = tds.get(1).select("a").get(0).text().toLowerCase().replace(" ", "");
            final float score = Float.parseFloat(tds.get(2).text().split("%")[0]);
            final int defcon = score < 20 ? 1 : score < 40 ? 2 : score < 60 ? 3 : score < 80.00 ? 4 : 5;
            final CountryRankingInfoValue value = new CountryRankingInfoValue(defcon, country, maxWorldRank, worldRank, yearOfData, score, NumberType.FLOAT, false, "Quality of Nationality Index", "% score", sources);
            list.add(value);
            countries.put(country, value.toString());
        }
        rankedJSON = toRankedJSON(list);
        handler.handle(null);
    }
}
