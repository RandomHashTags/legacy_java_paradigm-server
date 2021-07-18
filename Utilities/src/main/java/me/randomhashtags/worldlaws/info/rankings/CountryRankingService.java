package me.randomhashtags.worldlaws.info.rankings;

import me.randomhashtags.worldlaws.*;
import me.randomhashtags.worldlaws.info.service.CountryService;
import me.randomhashtags.worldlaws.location.SovereignStateInformationType;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.select.Elements;

import java.util.Collection;
import java.util.HashSet;

public interface CountryRankingService extends CountryService {
    String getURL();
    String getSiteName();
    String getSuffix();
    NumberType getValueType();
    int getMaxWorldRankOffset();
    int getYearOfData();

    @Override
    default FileType getFileType() {
        return FileType.COUNTRIES_RANKINGS;
    }

    @Override
    default SovereignStateInformationType getInformationType() {
        return SovereignStateInformationType.RANKINGS;
    }

    default Elements getRankingDocumentElements(String url, String targetElements) {
        return getRankingDocumentElements(url, targetElements, -1);
    }
    default Elements getRankingDocumentElements(String url, String targetElements, int index) {
        return getDocumentElements(FileType.COUNTRIES_RANKINGS, url, targetElements, index);
    }

    String getRankedJSON();
    void setRankedJSON(String rankedJSON);

    @Override
    default void loadData(CompletionHandler handler) {
        getJSONObject(this, new CompletionHandler() {
            @Override
            public void load(CompletionHandler handler) {
                handleJSONArrayCompletion(new JSONArray(loadData()), getMaxWorldRankOffset(), handler);
            }

            @Override
            public void handleJSONObject(JSONObject json) {
                handler.handleJSONObject(json);
            }
        });
    }
    String loadData();

    private void handleJSONArrayCompletion(JSONArray array, int maxWorldRankOffset, CompletionHandler handler) {
        final String url = getURL(), description = getInfo().getTitle(), suffix = getSuffix();
        final NumberType valueType = getValueType();
        final int maxWorldRank = array.length()+maxWorldRankOffset, yearOfData = getYearOfData();
        final String siteName = url.startsWith("https://en.wikipedia.org/wiki/") ? url.split("/wiki/")[1].replace("_", " ") : getSiteName();
        final EventSource source = new EventSource("Wikipedia: " + siteName, url);
        final EventSources sources = new EventSources(source);
        final Collection<CountryRankingInfoValue> list = new HashSet<>();

        final StringBuilder builder = new StringBuilder("{");
        boolean isFirst = true;
        for(Object obj : array) {
            final JSONObject json = (JSONObject) obj;
            final String country = json.getString("country");
            final CountryRankingInfoValue value = new CountryRankingInfoValue(json);
            value.setMaxWorldRank(maxWorldRank);
            if(yearOfData != -1) {
                value.setYearOfData(yearOfData);
            }
            value.setValueType(valueType);
            value.setDescription(description);
            value.setSuffix(suffix);
            value.setSources(sources);
            list.add(value);
            builder.append(isFirst ? "" : ",").append("\"").append(country).append("\":").append("{").append(value.toString()).append("}");
            isFirst = false;
        }
        builder.append("}");
        setRankedJSON(toRankedJSON(list));
        final JSONObject json = new JSONObject(builder.toString());
        setFileJSONObject(getFileType(), getInfo().getTitle(), json);
        handler.handleJSONObject(json);
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
