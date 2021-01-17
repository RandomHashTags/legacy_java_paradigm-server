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

public enum CO2Emissions implements CountryRankingService {
    INSTANCE;

    private String rankedJSON;
    private HashMap<String, String> countries;

    @Override
    public CountryInfo getInfo() {
        return CountryInfo.RANKING_CO2_EMISSIONS;
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
        final String url = "https://en.wikipedia.org/wiki/List_of_countries_by_carbon_dioxide_emissions";
        final Elements elements = getDocumentElements(url, "div.mw-parser-output table.wikitable", 0).select("tbody tr");
        elements.remove(0);
        elements.remove(0);
        elements.remove(0);
        elements.remove(0);
        elements.remove(0);
        final List<CountryRankingInfoValue> list = new ArrayList<>();
        final int maxWorldRank = elements.size(), yearOfData = 2017;
        final EventSource wikipedia = new EventSource("Wikipedia: List of countries by carbon dioxide emissions", url);
        final EventSources sources = new EventSources(wikipedia);
        for(Element element : elements) {
            final Elements tds = element.select("td");
            final Elements links = tds.select("a");
            final float emissions = Float.parseFloat(tds.get(3).text().replace(",", ""));
            final float percentOfTheWorld = Float.parseFloat(tds.get(4).text().replace("%", ""));
            final int defcon = emissions >= 1000.00 ? 1 : emissions >= 750.00 ? 2 : emissions >= 500.00 ? 3 : emissions >= 250.00 ? 4 : 5;
            for(Element link : links) {
                final String country = link.text().toLowerCase().replace(" ", "");
                final CountryRankingInfoValue value = new CountryRankingInfoValue(defcon, country, maxWorldRank, -1, yearOfData, emissions, NumberType.FLOAT, false, "CO2 Emissions", " Mt/year", sources);
                final List<CountryRankingInfoValueOther> values = new ArrayList<>();
                values.add(new CountryRankingInfoValueOther(percentOfTheWorld, NumberType.FLOAT, "Percent of the World", "%"));
                value.setOtherValues(values);
                list.add(value);
            }
        }
        list.sort(Comparator.comparingDouble(co2 -> co2.getValue().floatValue()));
        int worldRank = list.size();
        for(CountryRankingInfoValue obj : list) {
            obj.worldRank = worldRank;
            countries.put(obj.country, obj.toString());
            worldRank -= 1;
        }
        rankedJSON = toRankedJSON(list);
        handler.handle(null);
    }
}
