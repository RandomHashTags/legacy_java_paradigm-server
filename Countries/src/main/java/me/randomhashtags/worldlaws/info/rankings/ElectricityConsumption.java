package me.randomhashtags.worldlaws.info.rankings;

import me.randomhashtags.worldlaws.*;
import me.randomhashtags.worldlaws.info.rankings.CountryRankingInfoValue;
import me.randomhashtags.worldlaws.info.rankings.CountryRankingService;
import me.randomhashtags.worldlaws.location.CountryInfo;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.logging.Level;

public enum ElectricityConsumption implements CountryRankingService {
    INSTANCE;

    private String rankedJSON;
    private HashMap<String, String> countries;

    @Override
    public CountryInfo getInfo() {
        return CountryInfo.RANKING_ELECTRICITY_CONSUMPTION;
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
            WLLogger.log(Level.WARNING, "ElectricityConsumption - missing for country \"" + countryBackendID + "\"!");
        }
        return value;
    }

    private void refresh(CompletionHandler handler) {
        final long started = System.currentTimeMillis();
        countries = new HashMap<>();
        final String url = "https://en.wikipedia.org/wiki/List_of_countries_by_electricity_consumption";
        final Document doc = getDocument(url);
        if(doc != null) {
            final Elements elements = doc.select("div.mw-content-ltr div.mw-parser-output table.wikitable").get(0).select("tbody tr");
            elements.remove(0);
            elements.remove(0);
            final int maxWorldRank = elements.size();
            final EventSource wikipedia = new EventSource("Wikipedia: List of countries by electricity consumption", url);
            final EventSources sources = new EventSources(wikipedia);
            final Collection<CountryRankingInfoValue> list = new HashSet<>();
            for(Element element : elements) {
                final Elements tds = element.select("td");
                final int worldRank = Integer.parseInt(tds.get(0).text());
                final String country = tds.get(1).text().toLowerCase().replace(" ", "");
                final long consumption = Long.parseLong(tds.get(2).text().split("\\[")[0].replace(",", ""));
                final String[] yearValues = tds.get(3).text().split(" ");
                final int yearOfData = Integer.parseInt(yearValues[0]);
                final boolean isEstimate = yearValues.length > 1;
                final int defcon = -1;
                final CountryRankingInfoValue value = new CountryRankingInfoValue(defcon, country, maxWorldRank, worldRank, yearOfData, consumption, NumberType.LONG, isEstimate, "Electricity Consumption", " kW*h/yr", sources);
                list.add(value);
                countries.put(country, value.toString());
            }
            rankedJSON = toRankedJSON(list);
            WLLogger.log(Level.INFO, "ElectricityConsumption - refreshed " + countries.size() + " countries/territories (took " + (System.currentTimeMillis()-started) + "ms)");
            handler.handle(null);
        }
    }
}
