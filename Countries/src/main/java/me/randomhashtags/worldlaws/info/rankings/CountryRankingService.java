package me.randomhashtags.worldlaws.info.rankings;

import me.randomhashtags.worldlaws.Folder;
import me.randomhashtags.worldlaws.country.SovereignStateInformationType;
import me.randomhashtags.worldlaws.locale.JSONObjectTranslatable;
import me.randomhashtags.worldlaws.service.NewCountryServiceCentralData;
import org.json.JSONObject;
import org.jsoup.select.Elements;

import java.util.HashMap;

public interface CountryRankingService extends NewCountryServiceCentralData {
    HashMap<SovereignStateInformationType, JSONObjectTranslatable> RANKED_JSONS = new HashMap<>();
    String getURL();
    String getSiteName();

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

    @Override
    default JSONObjectTranslatable parseData(JSONObject json) {
        final JSONObjectTranslatable translatable = new JSONObjectTranslatable();
        for(String country : json.keySet()) {
            final JSONObject countryJSON = json.getJSONObject(country);
            final CountryRankingInfoValue value = CountryRankingInfoValue.parse(countryJSON);
            translatable.put(country, value.toJSONObject(), true);
        }
        return translatable;
    }

    default JSONObjectTranslatable getRankedJSON() {
        final SovereignStateInformationType type = getInformationType();
        if(!RANKED_JSONS.containsKey(type)) {
            final JSONObjectTranslatable string = loadData();
            final JSONObjectTranslatable json = new JSONObjectTranslatable();
            for(String key : string.keySet()) {
                final int worldRank = string.optInt("worldRank", -1);
                if(worldRank > 0) {
                    json.put(key, worldRank);
                }
            }
            RANKED_JSONS.put(type, json);
        }
        return RANKED_JSONS.get(type);
    }
}
