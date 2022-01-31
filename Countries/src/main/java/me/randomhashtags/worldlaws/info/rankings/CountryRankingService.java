package me.randomhashtags.worldlaws.info.rankings;

import me.randomhashtags.worldlaws.EventSource;
import me.randomhashtags.worldlaws.EventSources;
import me.randomhashtags.worldlaws.Folder;
import me.randomhashtags.worldlaws.country.SovereignStateInformationType;
import me.randomhashtags.worldlaws.service.CountryService;
import org.json.JSONObject;
import org.jsoup.select.Elements;

import java.util.Collection;
import java.util.HashMap;

public interface CountryRankingService extends CountryService {
    HashMap<SovereignStateInformationType, String> RANKED_JSONS = new HashMap<>();
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
        json.put("sources", sources.toJSONObject());
    }

    default String getRankedJSON() {
        final SovereignStateInformationType type = getInformationType();
        if(!RANKED_JSONS.containsKey(type)) {
            final String string = loadData();
            RANKED_JSONS.put(type, string);
        }
        return RANKED_JSONS.get(type);
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
