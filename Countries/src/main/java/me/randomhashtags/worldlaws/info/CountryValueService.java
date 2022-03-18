package me.randomhashtags.worldlaws.info;

import me.randomhashtags.worldlaws.EventSource;
import me.randomhashtags.worldlaws.EventSources;
import me.randomhashtags.worldlaws.Folder;
import me.randomhashtags.worldlaws.country.SovereignStateInformationType;
import me.randomhashtags.worldlaws.locale.JSONObjectTranslatable;
import me.randomhashtags.worldlaws.service.NewCountryService;
import org.json.JSONObject;
import org.jsoup.select.Elements;

public interface CountryValueService extends NewCountryService {
    String getURL();
    int getYearOfData();
    default EventSources getSources() {
        final String url = getURL(), siteName;
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
        return new EventSources(source);
    }

    @Override
    default Folder getFolder() {
        return Folder.COUNTRIES_VALUES;
    }
    @Override
    default SovereignStateInformationType getInformationType() {
        return SovereignStateInformationType.SINGLE_VALUES;
    }

    @Override
    default JSONObjectTranslatable parseData(JSONObject json) {
        final JSONObjectTranslatable translatable = new JSONObjectTranslatable();
        for(String country : json.keySet()) {
            final JSONObject countryJSON = json.getJSONObject(country);
            final CountrySingleValue value = CountrySingleValue.parse(countryJSON);
            translatable.put(country, value.toJSONObject());
            translatable.addTranslatedKey(country);
        }
        return translatable;
    }

    @Override
    default void insertCountryData(JSONObjectTranslatable dataJSON, JSONObjectTranslatable countryJSON) {
        countryJSON.addTranslatedKey("title");
        countryJSON.put("title", getInfo().getTitle());
        countryJSON.put("sources", getSources().toJSONObject());
        if(!countryJSON.has("yearOfData")) {
            countryJSON.put("yearOfData", getYearOfData());
        }
    }


    default Elements getValueDocumentElements(String url, String targetElements) {
        return getValueDocumentElements(url, targetElements, -1);
    }
    default Elements getValueDocumentElements(String url, String targetElements, int index) {
        return getDocumentElements(Folder.COUNTRIES_VALUES, url, targetElements, index);
    }
}
