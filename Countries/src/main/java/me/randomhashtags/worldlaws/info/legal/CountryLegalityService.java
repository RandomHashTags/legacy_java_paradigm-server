package me.randomhashtags.worldlaws.info.legal;

import me.randomhashtags.worldlaws.EventSource;
import me.randomhashtags.worldlaws.EventSources;
import me.randomhashtags.worldlaws.Folder;
import me.randomhashtags.worldlaws.WLUtilities;
import me.randomhashtags.worldlaws.country.SovereignStateInformationType;
import me.randomhashtags.worldlaws.info.CountryInfoKey;
import me.randomhashtags.worldlaws.locale.JSONObjectTranslatable;
import me.randomhashtags.worldlaws.service.NewCountryServiceCentralData;
import org.json.JSONObject;
import org.jsoup.select.Elements;

public interface CountryLegalityService extends NewCountryServiceCentralData {
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
    default JSONObjectTranslatable parseData(JSONObject json) {
        final JSONObjectTranslatable translatable = new JSONObjectTranslatable();
        for(String country : json.keySet()) {
            final JSONObject countryJSON = json.getJSONObject(country);
            final CountryInfoKey value = CountryInfoKey.parse(countryJSON);
            translatable.put(country, value.toJSONObject(), true);
        }
        return translatable;
    }

    @Override
    default void insertCountryData(JSONObjectTranslatable dataJSON, JSONObjectTranslatable countryJSON) {
        countryJSON.put("title", getInfo().getTitle(), true);
        if(!countryJSON.has("yearOfData")) {
            countryJSON.put("yearOfData", WLUtilities.getTodayYear());
        }
        countryJSON.put("sources", getSources().toJSONObject());
    }

    private EventSources getSources() {
        final String url = getURL();
        final String siteName = url.split("/wiki/")[1].replace("_", " ");
        final EventSource source = new EventSource("Wikipedia: " + siteName, url);
        return new EventSources(source);
    }
}
