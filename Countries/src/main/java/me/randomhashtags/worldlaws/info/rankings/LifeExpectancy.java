package me.randomhashtags.worldlaws.info.rankings;

import me.randomhashtags.worldlaws.*;
import me.randomhashtags.worldlaws.info.rankings.CountryRankingInfoValue;
import me.randomhashtags.worldlaws.info.rankings.CountryRankingInfoValueOther;
import me.randomhashtags.worldlaws.info.rankings.CountryRankingService;
import me.randomhashtags.worldlaws.location.CountryInfo;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.*;
import java.util.logging.Level;

public enum LifeExpectancy implements CountryRankingService {
    INSTANCE;

    private String rankedJSON;
    private HashMap<String, String> countries;

    @Override
    public CountryInfo getInfo() {
        return CountryInfo.RANKING_LIFE_EXPECTANCY;
    }

    @Override
    public void getRankedJSON(CompletionHandler handler) {
        if(rankedJSON != null) {
            handler.handle(rankedJSON);
        } else {
            refresh(new CompletionHandler() {
                @Override
                public void handle(Object object) {
                    handler.handle(rankedJSON);
                }
            });
        }
    }

    @Override
    public void getValue(String countryBackendID, CompletionHandler handler) {
        if(countries != null) {
            final String value = getValue(countryBackendID);
            handler.handle(value);
        } else {
            refresh(new CompletionHandler() {
                @Override
                public void handle(Object object) {
                    final String value = getValue(countryBackendID);
                    handler.handle(value);
                }
            });
        }
    }
    private String getValue(String countryBackendID) {
        final String value = countries.getOrDefault(countryBackendID, "null");
        if(value.equals("null")) {
            WLLogger.log(Level.WARNING,"LifeExpectancy - missing value for country \"" + countryBackendID + "\"!");
        }
        return value;
    }

    private void refresh(CompletionHandler handler) {
        final long started = System.currentTimeMillis();
        countries = new HashMap<>();
        final String url = "https://en.wikipedia.org/wiki/List_of_countries_by_life_expectancy";
        final Elements elements = getDocument(url).select("div.mw-parser-output table.wikitable").get(0).select("tbody tr");
        elements.remove(0);
        elements.remove(0);
        elements.removeIf(element -> element.select("td").get(0).text().equals("—"));
        final int maxWorldRank = elements.size(), yearOfData = 2018;
        int lastWorldRank = -1;
        final EventSource wikipedia = new EventSource("Wikipedia: List of countries by life expectancy", url);
        final EventSources sources = new EventSources(wikipedia);
        final Collection<CountryRankingInfoValue> list = new HashSet<>();
        for(Element element : elements) {
            final Elements tds = element.select("td");
            final int max = tds.size();
            final String targetRank = tds.get(0).text();
            final int worldRank = max == 5 ? lastWorldRank : Integer.parseInt(targetRank);
            lastWorldRank = worldRank;
            final String country = tds.get(max-5).select("a").get(0).text().toLowerCase().replace(" ", ""), femaleString = tds.get(max-3).text(), maleString = tds.get(max-2).text();
            final float average = Float.parseFloat(tds.get(max-4).text()), female = femaleString.isEmpty() ? -1 : Float.parseFloat(femaleString), male = maleString.isEmpty() ? -1 : Float.parseFloat(maleString);
            final int defcon = average <= 60.00 ? 1 : average <= 65.00 ? 2 : average <= 70.00 ? 3 : average <= 75.00 ? 4 : 5;
            final CountryRankingInfoValue value = new CountryRankingInfoValue(defcon, country, maxWorldRank, worldRank, yearOfData, average, NumberType.FLOAT, true, "Life Expectancy", " years", sources);
            final List<CountryRankingInfoValueOther> values = new ArrayList<>();
            values.add(new CountryRankingInfoValueOther(female, NumberType.FLOAT, "Female Life Expectancy", " years"));
            values.add(new CountryRankingInfoValueOther(male, NumberType.FLOAT, "Male Life Expectancy", " years"));
            value.setOtherValues(values);
            list.add(value);
            countries.put(country, value.toString());
        }
        rankedJSON = toRankedJSON(list);
        WLLogger.log(Level.INFO,"LifeExpectancy - refreshed " + countries.size() + " countries/regions (took " + (System.currentTimeMillis()-started) + "ms)");
        handler.handle(null);
    }
}
