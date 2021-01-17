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

public enum Population implements CountryRankingService {
    INSTANCE;

    private String rankedJSON;
    private HashMap<String, String> countries;

    @Override
    public CountryInfo getInfo() {
        return CountryInfo.RANKING_POPULATION;
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
        final String url = "https://en.wikipedia.org/wiki/List_of_countries_and_dependencies_by_population";
        final Elements elements = getDocumentElements(url, "div.mw-parser-output table.wikitable", 0).select("tbody tr");
        elements.remove(0);
        final int maxWorldRank = elements.size(), yearOfData = 2020;
        final EventSource wikipedia = new EventSource("Wikipedia: List of countries and dependencies by population", url);
        final EventSources sources = new EventSources(wikipedia);
        final Collection<CountryRankingInfoValue> list = new HashSet<>();
        for(Element element : elements) {
            final Elements tds = element.select("td");
            final String targetWorldRank = element.select("th").get(0).text();
            if(!targetWorldRank.equals("–")) {
                try {
                    final int worldRank = Integer.parseInt(targetWorldRank);
                    final String country = tds.get(0).text().split("\\[")[0].toLowerCase().replace(" ", "");
                    final long population = Long.parseLong(tds.get(1).text().replace(",", ""));
                    final int defcon = -1;
                    final CountryRankingInfoValue value = new CountryRankingInfoValue(defcon, country, maxWorldRank, worldRank, yearOfData, population, NumberType.LONG, false, "World Population", "", sources);
                    list.add(value);
                    countries.put(country, value.toString());
                } catch (Exception ignored) {
                }
            }
        }
        rankedJSON = toRankedJSON(list);
        handler.handle(null);
    }
}
