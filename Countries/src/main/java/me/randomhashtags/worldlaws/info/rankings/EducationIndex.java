package me.randomhashtags.worldlaws.info.rankings;

import me.randomhashtags.worldlaws.CompletionHandler;
import me.randomhashtags.worldlaws.EventSource;
import me.randomhashtags.worldlaws.EventSources;
import me.randomhashtags.worldlaws.NumberType;
import me.randomhashtags.worldlaws.location.CountryInfo;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.*;

public enum EducationIndex implements CountryRankingService {
    INSTANCE;

    private String rankedJSON;
    private HashMap<String, String> countries;

    @Override
    public CountryInfo getInfo() {
        return CountryInfo.RANKING_EDUCATION_INDEX;
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
        final String url = "https://en.wikipedia.org/wiki/Education_Index";
        final Elements trs = getRankingDocumentElements(url, "div.mw-parser-output table.wikitable", 0).select("tbody tr");
        final EventSource wikipedia = new EventSource("Wikipedia: Education Index", url);
        final EventSources sources = new EventSources(wikipedia);

        trs.remove(0);
        final int maxWorldRank = trs.size(), yearOfData = 2015;
        final Collection<CountryRankingInfoValue> list = new HashSet<>();
        for(Element element : trs) {
            final Elements tds = element.select("td");
            final int worldRank = Integer.parseInt(tds.get(0).text());
            final String country = tds.get(1).text().toLowerCase().replace("Federation", "").replace(" ", "").split("\\(")[0];
            final float index = Float.parseFloat(tds.get(2).text());
            final int defcon = index < 0.500 ? 1 : index < 0.600 ? 2 : index < 0.700 ? 3 : index < 0.800 ? 4 : 5;
            final CountryRankingInfoValue value = new CountryRankingInfoValue(defcon, country, maxWorldRank, worldRank, yearOfData, index, NumberType.FLOAT, false, "Education Index", " index", sources);
            final List<CountryRankingInfoValueOther> otherValues = new ArrayList<>();
            otherValues.add(new CountryRankingInfoValueOther(Float.parseFloat(tds.get(3).text()), NumberType.FLOAT, "Expected years of schooling", " years"));
            otherValues.add(new CountryRankingInfoValueOther(Float.parseFloat(tds.get(4).text()), NumberType.FLOAT, "Mean years of schooling", " years"));
            value.setOtherValues(otherValues);
            list.add(value);
            countries.put(country, value.toString());
        }
        rankedJSON = toRankedJSON(list);
        handler.handle(null);
    }
}
