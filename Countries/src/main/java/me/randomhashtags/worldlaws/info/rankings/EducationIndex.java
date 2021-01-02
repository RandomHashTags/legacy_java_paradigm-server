package me.randomhashtags.worldlaws.info.rankings;

import me.randomhashtags.worldlaws.*;
import me.randomhashtags.worldlaws.location.CountryInfo;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.*;
import java.util.logging.Level;

public enum EducationIndex implements CountryRankingService {
    INSTANCE;

    private String rankedJSON;
    private HashMap<String, String> countries;

    @Override
    public CountryInfo getInfo() {
        return CountryInfo.RANKING_EDUCATION_INDEX;
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
            WLLogger.log(Level.WARNING,"EducationIndex - missing value for country \"" + countryBackendID + "\"!");
        }
        return value;
    }

    private void refresh(CompletionHandler handler) {
        final long started = System.currentTimeMillis();
        countries = new HashMap<>();
        final String url = "https://en.wikipedia.org/wiki/Education_Index";
        final Document doc = getDocument(url);
        if(doc != null) {
            final EventSource wikipedia = new EventSource("Wikipedia: Education Index", url);
            final EventSources sources = new EventSources(wikipedia);

            final Elements trs = doc.select("div.mw-parser-output table.wikitable").get(0).select("tbody tr");
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
            WLLogger.log(Level.INFO, "EducationIndex - refreshed " + countries.size() + " countries (took " + (System.currentTimeMillis()-started) + "ms)");
            handler.handle(null);
        }
    }
}
