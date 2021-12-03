package me.randomhashtags.worldlaws.info.rankings;

import me.randomhashtags.worldlaws.CompletionHandler;
import me.randomhashtags.worldlaws.EventSource;
import me.randomhashtags.worldlaws.EventSources;
import me.randomhashtags.worldlaws.Folder;
import me.randomhashtags.worldlaws.country.SovereignStateInformationType;
import me.randomhashtags.worldlaws.info.service.CountryService;
import org.json.JSONObject;
import org.jsoup.select.Elements;

import java.util.Collection;

public interface CountryRankingService extends CountryService {
    String getURL();
    String getSiteName();
    String getSuffix();
    NumberType getValueType();
    int getYearOfData();

    @Override
    default Folder getFolder() {
        return Folder.COUNTRIES_RANKINGS;
    }

    @Override
    default SovereignStateInformationType getInformationType() {
        return SovereignStateInformationType.RANKINGS;
    }

    default Elements getRankingDocumentElements(String url, String targetElements) {
        return getRankingDocumentElements(url, targetElements, -1);
    }
    default Elements getRankingDocumentElements(String url, String targetElements, int index) {
        return getDocumentElements(Folder.COUNTRIES_RANKINGS, url, targetElements, index);
    }

    String getRankedJSON();
    void setRankedJSON(String rankedJSON);

    @Override
    default void loadData(CompletionHandler handler) {
        handler.handleJSONObject(loadData());
    }
    JSONObject loadData();

    @Override
    default void insertValuesIntoCountryValueJSONObject(JSONObject json) {
        final int yearOfData = getYearOfData();
        final String url = getURL().replace("%year%", Integer.toString(yearOfData)), suffix = getSuffix();
        json.put("suffix", suffix);
        if(!json.has("yearOfData")) {
            json.put("yearOfData", yearOfData);
        }
        json.put("valueType", getValueType().name());

        final String siteName = url.startsWith("https://en.wikipedia.org/wiki/") ? url.split("/wiki/")[1].replace("_", " ") : getSiteName();
        final EventSource source = new EventSource("Wikipedia: " + siteName, url);
        final EventSources sources = new EventSources(source);
        json.put("sources", sources.getJSON());
    }

    default void getRankedJSON(CompletionHandler handler) {
        final String rankedJSON = getRankedJSON();
        if(rankedJSON != null) {
            handler.handleString(rankedJSON);
        } else {
            loadData(new CompletionHandler() {
                @Override
                public void handleString(String string) {
                    handler.handleString(getRankedJSON());
                }
            });
        }
    }

    private String toRankedJSON(Collection<CountryRankingInfoValue> values) {
        final StringBuilder builder = new StringBuilder("{");
        boolean isFirst = true;
        for(CountryRankingInfoValue value : values) {
            builder.append(isFirst ? "" : ",").append("\"").append(value.country.replace(" ", "")).append("\":").append(value.getWorldRank());
            isFirst = false;
        }
        builder.append("}");
        return builder.toString();
    }
}
