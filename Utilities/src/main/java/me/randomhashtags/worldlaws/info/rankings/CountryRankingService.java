package me.randomhashtags.worldlaws.info.rankings;

import me.randomhashtags.worldlaws.*;
import me.randomhashtags.worldlaws.info.service.CountryService;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.select.Elements;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;

public interface CountryRankingService extends CountryService {
    void setCountries(HashMap<String, String> countries);
    String getURL();
    String getSuffix();
    NumberType getValueType();
    int getMaxWorldRankOffset();
    int getYearOfData();

    @Override
    default FileType getFileType() {
        return FileType.COUNTRIES_RANKINGS;
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
    default void refresh(CompletionHandler handler) {
        getJSONArray(this, new CompletionHandler() {
            @Override
            public void load(CompletionHandler handler) {
                handler.handle(loadData());
            }

            @Override
            public void handleJSONArray(JSONArray array) {
                handleJSONArrayCompletion(array, getMaxWorldRankOffset(), handler);
            }
        });
    }
    String loadData();

    private void handleJSONArrayCompletion(JSONArray array, int maxWorldRankOffset, CompletionHandler handler) {
        final String url = getURL(), description = getInfo().getTitle(), suffix = getSuffix();
        final NumberType valueType = getValueType();
        final int maxWorldRank = array.length()+maxWorldRankOffset, yearOfData = getYearOfData();
        final String siteName = getURL().split("/wiki/")[1].replace("_", " ");
        final EventSource source = new EventSource("Wikipedia: " + siteName, url);
        final EventSources sources = new EventSources(source);
        final Collection<CountryRankingInfoValue> list = new HashSet<>();
        final HashMap<String, String> countries = new HashMap<>();
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
            countries.put(country, value.toString());
        }
        setCountries(countries);
        setRankedJSON(toRankedJSON(list));
        handler.handle(null);
    }

    default void getRankedJSON(CompletionHandler handler) {
        final String rankedJSON = getRankedJSON();
        if(rankedJSON != null) {
            handler.handle(rankedJSON);
        } else {
            refresh(new CompletionHandler() {
                @Override
                public void handle(Object object) {
                    handler.handle(getRankedJSON());
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
