package me.randomhashtags.worldlaws.info;

import me.randomhashtags.worldlaws.CompletionHandler;
import me.randomhashtags.worldlaws.EventSource;
import me.randomhashtags.worldlaws.EventSources;
import me.randomhashtags.worldlaws.Folder;
import me.randomhashtags.worldlaws.country.SovereignStateInformationType;
import me.randomhashtags.worldlaws.info.service.CountryService;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.select.Elements;

public interface CountryValueService extends CountryService {
    String getURL();
    int getYearOfData();

    @Override
    default Folder getFolder() {
        return Folder.COUNTRIES_VALUES;
    }
    @Override
    default SovereignStateInformationType getInformationType() {
        return SovereignStateInformationType.SINGLE_VALUES;
    }

    default Elements getValueDocumentElements(String url, String targetElements) {
        return getValueDocumentElements(url, targetElements, -1);
    }
    default Elements getValueDocumentElements(String url, String targetElements, int index) {
        return getDocumentElements(Folder.COUNTRIES_VALUES, url, targetElements, index);
    }

    @Override
    default void loadData(CompletionHandler handler) {
        getJSONObject(new CompletionHandler() {
            @Override
            public void load(CompletionHandler handler) {
                final JSONArray array = new JSONArray(loadData());
                handleJSONArrayCompletion(array, handler);
            }
        });
    }
    String loadData();

    private void handleJSONArrayCompletion(JSONArray array, CompletionHandler handler) {
        final String url = getURL(), title = getInfo().getTitle();
        final int yearOfData = getYearOfData();
        final String siteName;
        switch (url) {
            case "https://aceproject.org/epic-en/CDTable?view=country&question=VR001":
                siteName = "ACE Electoral Knowledge Network";
                break;
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
            final CountrySingleValue value = new CountrySingleValue(json);
            value.setTitle(title);
            value.setSources(sources);
            if(yearOfData != -1) {
                value.setYearOfData(yearOfData);
            }
            builder.append(isFirst ? "" : ",").append("\"").append(country).append("\":{").append(value.toString()).append("}");
            isFirst = false;
        }
        builder.append("}");
        handler.handleString(builder.toString());
    }
}
