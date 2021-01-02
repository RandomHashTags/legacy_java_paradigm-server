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

public enum CO2Emissions implements CountryRankingService {
    INSTANCE;

    private String rankedJSON;
    private HashMap<String, String> countries;

    @Override
    public CountryInfo getInfo() {
        return CountryInfo.RANKING_CO2_EMISSIONS;
    }

    @Override
    public void getRankedJSON(CompletionHandler handler) {
        if(rankedJSON != null) {
            handler.handle(rankedJSON);
        } else {
            refreshEmissions(new CompletionHandler() {
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
            refreshEmissions(new CompletionHandler() {
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
            WLLogger.log(Level.WARNING, "CO2Emissions - missing for country \"" + countryBackendID + "\"!");
        }
        return value;
    }

    private void refreshEmissions(CompletionHandler handler) {
        final long started = System.currentTimeMillis();
        countries = new HashMap<>();
        final String url = "https://en.wikipedia.org/wiki/List_of_countries_by_carbon_dioxide_emissions";
        final Document doc = getDocument(url);
        if(doc != null) {
            final Elements elements = doc.select("div.mw-parser-output table.wikitable").get(0).select("tbody tr");
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
            WLLogger.log(Level.INFO, "CO2Emissions - refreshed (took " + (System.currentTimeMillis()-started) + "ms)");
            handler.handle(null);
        }
    }
}
