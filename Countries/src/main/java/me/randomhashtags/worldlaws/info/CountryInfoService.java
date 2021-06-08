package me.randomhashtags.worldlaws.info;

import me.randomhashtags.worldlaws.CompletionHandler;
import me.randomhashtags.worldlaws.EventSource;
import me.randomhashtags.worldlaws.EventSources;
import me.randomhashtags.worldlaws.FileType;
import me.randomhashtags.worldlaws.info.service.CountryService;
import me.randomhashtags.worldlaws.location.CountryInformationType;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.select.Elements;

public interface CountryInfoService extends CountryService {
    String getURL();
    int getYearOfData();

    @Override
    default FileType getFileType() {
        return FileType.COUNTRIES_INFO;
    }
    @Override
    default CountryInformationType getInformationType() {
        return CountryInformationType.INFORMATION;
    }

    default Elements getInfoDocumentElements(String url, String targetElements) {
        return getInfoDocumentElements(url, targetElements, -1);
    }
    default Elements getInfoDocumentElements(String url, String targetElements, int index) {
        return getDocumentElements(FileType.COUNTRIES_INFO, url, targetElements, index);
    }

    @Override
    default void loadData(CompletionHandler handler) {
        getJSONObject(this, new CompletionHandler() {
            @Override
            public void load(CompletionHandler handler) {
                handleJSONArrayCompletion(new JSONArray(loadData()), handler);
            }

            @Override
            public void handleJSONObject(JSONObject json) {
                handler.handleJSONObject(json);
            }
        });
    }
    String loadData();

    private void handleJSONArrayCompletion(JSONArray array, CompletionHandler handler) {
        final String url = getURL(), title = getInfo().getTitle();
        final int yearOfData = getYearOfData();
        final String siteName;
        switch (url) {
            default:
                final boolean isWikipedia = url.contains("/wiki/");
                siteName = isWikipedia ? "Wikipedia: " + url.split("/wiki/")[1].replace("_", " ") : "Unknown";
                break;
        }
        final EventSource source = new EventSource(siteName, url);
        final EventSources sources = new EventSources(source);

        final StringBuilder builder = new StringBuilder("{");
        boolean isFirst = true;
        for(Object obj : array) {
            final JSONObject json = (JSONObject) obj;
            final String country = json.getString("country");
            final CountryInfoKey key = new CountryInfoKey(json);
            key.setTitle(title);
            key.setSources(sources);
            if(yearOfData != -1) {
                key.setYearOfData(yearOfData);
            }
            builder.append(isFirst ? "" : ",").append("\"").append(country).append("\":").append("{").append(key.toString()).append("}");
            isFirst = false;
        }
        builder.append("}");
        final JSONObject json = new JSONObject(builder.toString());
        setFileJSONObject(getFileType(), getInfo().getTitle(), json);
        handler.handleJSONObject(json);
    }
}
