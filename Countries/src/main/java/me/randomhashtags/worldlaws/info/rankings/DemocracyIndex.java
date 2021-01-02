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

public enum DemocracyIndex implements CountryRankingService {
    INSTANCE;

    private String rankedJSON;
    private HashMap<String, String> countries;

    @Override
    public CountryInfo getInfo() {
        return CountryInfo.RANKING_DEMOCRACY_INDEX;
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
            WLLogger.log(Level.WARNING,"DemocracyIndex - missing value for country \"" + countryBackendID + "\"!");
        }
        return value;
    }

    private void refresh(CompletionHandler handler) {
        final long started = System.currentTimeMillis();
        countries = new HashMap<>();
        final String url = "https://en.wikipedia.org/wiki/Democracy_Index";
        final Document doc = getDocument(url);
        if(doc != null) {
            final EventSource wikipedia = new EventSource("Wikipedia: Democracy Index", url);
            final EventSources sources = new EventSources(wikipedia);

            final Elements trs = doc.select("div.mw-parser-output table.wikitable").get(0).select("tbody tr");
            trs.remove(0);
            trs.remove(trs.last());
            final int maxWorldRank = trs.size(), yearOfData = 2019;
            int previousWorldRank = -1;
            final Collection<CountryRankingInfoValue> list = new HashSet<>();
            for(Element element : trs) {
                final Elements tds = element.select("td");
                final boolean hasRank = tds.get(0).text().matches("[0-9]+");
                final int worldRank = hasRank ? Integer.parseInt(tds.get(0).text()) : previousWorldRank;
                final String country = tds.get(hasRank ? 1 : 0).text().toLowerCase().split("\\[")[0].replace(" ", "");
                final float score = Float.parseFloat(tds.get(hasRank ? 2 : 1).text());
                final float electoralProcessAndPluralism = Float.parseFloat(tds.get(hasRank ? 3 : 2).text());
                final float functioningOfGovernment = Float.parseFloat(tds.get(hasRank ? 4 : 3).text());
                final float politicalParticipation = Float.parseFloat(tds.get(hasRank ? 5 : 4).text());
                final float politicalCulture = Float.parseFloat(tds.get(hasRank ? 6 : 5).text());
                final float civilLiberties = Float.parseFloat(tds.get(hasRank ? 7 : 6).text());
                final int defcon = score <= 4.00 ? 1 : score <= 6.00 ? 2 : score <= 7.00 ? 3 : score <= 8.00 ? 4 : 5;
                final CountryRankingInfoValue value = new CountryRankingInfoValue(defcon, country, maxWorldRank, worldRank, yearOfData, score, NumberType.FLOAT, false, "Democracy Index", " score", sources);
                final List<CountryRankingInfoValueOther> values = new ArrayList<>();
                values.add(new CountryRankingInfoValueOther(electoralProcessAndPluralism, NumberType.FLOAT, "Electoral process and pluralism", " score"));
                values.add(new CountryRankingInfoValueOther(functioningOfGovernment, NumberType.FLOAT, "Functioning of government", " score"));
                values.add(new CountryRankingInfoValueOther(politicalParticipation, NumberType.FLOAT, "Political participation", " score"));
                values.add(new CountryRankingInfoValueOther(politicalCulture, NumberType.FLOAT, "Political culture", " score"));
                values.add(new CountryRankingInfoValueOther(civilLiberties, NumberType.FLOAT, "Civil liberties", " score"));
                value.setOtherValues(values);
                list.add(value);
                countries.put(country, value.toString());
                previousWorldRank = worldRank;
            }
            rankedJSON = toRankedJSON(list);
            WLLogger.log(Level.INFO, "DemocracyIndex - refreshed (took " + (System.currentTimeMillis()-started) + "ms)");
            handler.handle(null);
        }
    }
}
