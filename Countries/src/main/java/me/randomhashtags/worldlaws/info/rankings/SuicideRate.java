package me.randomhashtags.worldlaws.info.rankings;

import me.randomhashtags.worldlaws.*;
import me.randomhashtags.worldlaws.info.rankings.CountryRankingInfoValue;
import me.randomhashtags.worldlaws.info.rankings.CountryRankingInfoValueOther;
import me.randomhashtags.worldlaws.info.rankings.CountryRankingService;
import me.randomhashtags.worldlaws.location.CountryInfo;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.*;
import java.util.logging.Level;

public enum SuicideRate implements CountryRankingService {
    INSTANCE;

    private String rankedJSON;
    private HashMap<String, String> countries;

    @Override
    public CountryInfo getInfo() {
        return CountryInfo.RANKING_SUICIDE_RATE;
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
            WLLogger.log(Level.WARNING,"SuicideRate - missing value for country \"" + countryBackendID + "\"!");
        }
        return value;
    }

    private void refresh(CompletionHandler handler) {
        final long started = System.currentTimeMillis();
        countries = new HashMap<>();
        final String url = "https://en.wikipedia.org/wiki/List_of_countries_by_suicide_rate";
        final Document doc = getDocument(url);
        if(doc != null) {
            final EventSource wikipedia = new EventSource("Wikipedia: List of countries by suicide rate", url);
            final EventSources sources = new EventSources(wikipedia);

            final Elements trs = doc.select("div.mw-parser-output table.wikitable").get(2).select("tbody tr");
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
            WLLogger.log(Level.INFO, "SuicideRates - refreshed (took " + (System.currentTimeMillis()-started) + "ms)");
            handler.handle(null);
        }
    }
}
