package me.randomhashtags.worldlaws.info.rankings;

import me.randomhashtags.worldlaws.CompletionHandler;
import me.randomhashtags.worldlaws.EventSource;
import me.randomhashtags.worldlaws.EventSources;
import me.randomhashtags.worldlaws.NumberType;
import me.randomhashtags.worldlaws.location.CountryInfo;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.*;

public enum CivilianFirearms implements CountryRankingService {
    INSTANCE;

    private String rankedJSON;
    private HashMap<String, String> countries;

    @Override
    public CountryInfo getInfo() {
        return CountryInfo.RANKING_CIVILIAN_FIREARMS;
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
        final String url = "https://en.wikipedia.org/wiki/Estimated_number_of_civilian_guns_per_capita_by_country";
        final Elements elements = getDocumentElements(url, "div.mw-parser-output table.wikitable", 0).select("tbody tr");
        elements.remove(0);
        elements.remove(0);
        final EventSource wikipedia = new EventSource("Wikipedia: Estimated number of civilian guns per capita by country", url);
        final EventSources sources = new EventSources(wikipedia);
        final int maxWorldRank = elements.size(), yearOfData = 2017;
        final Collection<CountryRankingInfoValue> list = new HashSet<>();
        for(Element element : elements) {
            final Elements tds = element.select("td");
            final String estimate = tds.get(6).text().replace(",", ""), registeredFirearms = tds.get(8).text().replace(",", ""), unregisteredFirearms = tds.get(9).text().replace(",", "");
            final int worldRank = Integer.parseInt(tds.get(0).text());
            final int estimateInCivilianPossession = estimate.matches("[0-9]+") ? Integer.parseInt(estimate) : -1;
            final int registered = registeredFirearms.matches("[0-9]+") ? Integer.parseInt(registeredFirearms) : -1;
            final int unregistered = unregisteredFirearms.matches("[0-9]+") ? Integer.parseInt(unregisteredFirearms) : -1;
            final String country = tds.get(1).text().toLowerCase().replace(" ", "");
            final float estimatePer100 = Float.parseFloat(tds.get(2).text());
            final int defcon = -1;
            final CountryRankingInfoValue value = new CountryRankingInfoValue(defcon, country, maxWorldRank, worldRank, yearOfData, estimatePer100, NumberType.FLOAT, true, "Civilian Firearms", " per 100 persons", sources);
            final List<CountryRankingInfoValueOther> values = new ArrayList<>();
            values.add(new CountryRankingInfoValueOther(estimateInCivilianPossession, NumberType.INTEGER, "Estimate in Civilian Possession", null));
            values.add(new CountryRankingInfoValueOther(registered, NumberType.INTEGER, "Registered", null));
            values.add(new CountryRankingInfoValueOther(unregistered, NumberType.INTEGER, "Unregistered", null));
            value.setOtherValues(values);
            list.add(value);
            countries.put(country, value.toString());
        }
        rankedJSON = toRankedJSON(list);
        handler.handle(null);
    }
}
