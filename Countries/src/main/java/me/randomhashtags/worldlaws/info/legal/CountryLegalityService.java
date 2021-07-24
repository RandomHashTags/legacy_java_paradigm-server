package me.randomhashtags.worldlaws.info.legal;

import me.randomhashtags.worldlaws.CompletionHandler;
import me.randomhashtags.worldlaws.EventSource;
import me.randomhashtags.worldlaws.EventSources;
import me.randomhashtags.worldlaws.Folder;
import me.randomhashtags.worldlaws.info.CountryInfoKey;
import me.randomhashtags.worldlaws.info.service.CountryService;
import me.randomhashtags.worldlaws.location.SovereignStateInformationType;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.select.Elements;

public interface CountryLegalityService extends CountryService {
    String getURL();
    int getYearOfData();

    @Override
    default Folder getFolder() {
        return Folder.COUNTRIES_LEGALITIES;
    }
    @Override
    default SovereignStateInformationType getInformationType() {
        return SovereignStateInformationType.LEGALITIES;
    }

    default Elements getLegalityDocumentElements(String url, String targetElements) {
        return getLegalityDocumentElements(url, targetElements, -1);
    }
    default Elements getLegalityDocumentElements(String url, String targetElements, int index) {
        return getDocumentElements(Folder.COUNTRIES_LEGALITIES, url, targetElements, index);
    }

    @Override
    default void loadData(CompletionHandler handler) {
        getJSONObject(new CompletionHandler() {
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
        final String siteName = url.split("/wiki/")[1].replace("_", " ");
        final EventSource source = new EventSource("Wikipedia: " + siteName, url);
        final EventSources sources = new EventSources(source);

        final StringBuilder builder = new StringBuilder("{");
        boolean isFirst = true;
        for(Object obj : array) {
            final JSONObject valueJSON = (JSONObject) obj;
            final String country = valueJSON.getString("country");
            final CountryInfoKey key = new CountryInfoKey(valueJSON);
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
        handler.handleJSONObject(json);
    }
}
