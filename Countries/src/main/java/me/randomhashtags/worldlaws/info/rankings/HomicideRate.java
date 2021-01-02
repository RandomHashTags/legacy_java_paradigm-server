package me.randomhashtags.worldlaws.info.rankings;

import me.randomhashtags.worldlaws.*;
import me.randomhashtags.worldlaws.info.rankings.CountryRankingInfoValue;
import me.randomhashtags.worldlaws.info.rankings.CountryRankingService;
import me.randomhashtags.worldlaws.location.CountryInfo;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;

public enum HomicideRate implements CountryRankingService {
    INSTANCE;

    private String rankedJSON;
    private HashMap<String, String> countries;

    @Override
    public CountryInfo getInfo() {
        return CountryInfo.RANKING_HOMICIDE_RATE;
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
            WLLogger.log(Level.WARNING, "HomicideRate - missing for country \"" + countryBackendID + "\"!");
        }
        return value;
    }

    private void refresh(CompletionHandler handler) {
        final long started = System.currentTimeMillis();
        countries = new HashMap<>();
        final String url = "https://en.wikipedia.org/wiki/List_of_countries_by_intentional_homicide_rate";
        final Document doc = getDocument(url);
        if(doc != null) {
            final Elements elements = doc.select("div.mw-parser-output table tbody tr td table.wikitable").get(1).select("tbody tr");
            elements.remove(0);
            final int maxWorldRank = elements.size();
            final EventSources sources = new EventSources(new EventSource("Wikipedia: List of countries by intentional homicide rate", url));
            final List<CountryRankingInfoValue> list = new ArrayList<>();
            for(Element element : elements) {
                final Elements headers = element.select("th");
                final boolean hasHeader = !headers.isEmpty();
                final Elements tds = element.select("td");
                final String country = (hasHeader ? headers : tds).get(0).text().toLowerCase().replace(" ", "");
                final int yearOfData = Integer.parseInt(tds.get(hasHeader ? 4 : 5).text());
                final float rate = Float.parseFloat(tds.get(hasHeader ? 2 : 3).text());
                final int defcon = rate > 20.00 ? 1 : rate > 10 ? 2 : rate > 5 ? 3 : rate > 2.50 ? 4 : 5;
                final CountryRankingInfoValue value = new CountryRankingInfoValue(defcon, country, maxWorldRank, -1, yearOfData, rate, NumberType.FLOAT, false, "Homicide Rate", " per 100,000", sources);
                list.add(value);
            }
            list.sort(Comparator.comparingDouble(element -> element.getValue().floatValue()));
            int worldRank = maxWorldRank;
            for(CountryRankingInfoValue obj : list) {
                obj.worldRank = worldRank;
                worldRank -= 1;
                countries.put(obj.country, obj.toString());
            }
            rankedJSON = toRankedJSON(list);
            WLLogger.log(Level.INFO, "HomicideRate - refreshed (took " + (System.currentTimeMillis()-started) + "ms)");
            handler.handle(null);
        }
    }
}
