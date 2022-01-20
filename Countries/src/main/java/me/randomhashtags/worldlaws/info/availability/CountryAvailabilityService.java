package me.randomhashtags.worldlaws.info.availability;

import me.randomhashtags.worldlaws.CompletionHandler;
import me.randomhashtags.worldlaws.Folder;
import me.randomhashtags.worldlaws.WLUtilities;
import me.randomhashtags.worldlaws.country.SovereignStateInfo;
import me.randomhashtags.worldlaws.country.SovereignStateInformationType;
import me.randomhashtags.worldlaws.info.service.CountryService;
import org.json.JSONArray;
import org.jsoup.select.Elements;

import java.util.HashMap;

public interface CountryAvailabilityService extends CountryService {
    HashMap<SovereignStateInfo, JSONArray> AVAILABILITY_VALUES = new HashMap<>();

    @Override
    default Folder getFolder() {
        return Folder.COUNTRIES_SERVICES_AVAILABILITIES;
    }

    @Override
    default SovereignStateInformationType getInformationType() {
        return SovereignStateInformationType.AVAILABILITIES;
    }

    default Elements getAvailabilityDocumentElements(String url, String targetElements) {
        return getAvailabilityDocumentElements(url, targetElements, -1);
    }
    default Elements getAvailabilityDocumentElements(String url, String targetElements, int index) {
        return getDocumentElements(getFolder(), url, targetElements, index);
    }

    default CountryAvailability getAvailability(boolean value) {
        return new CountryAvailability(getInfo().getTitle(), getPrimaryCategory(), getImageURL(), value);
    }
    default String loadOnlyTrue(String...countries) {
        final StringBuilder builder = new StringBuilder("[");
        boolean isFirst = true;
        for(String country : countries) {
            builder.append(isFirst ? "" : ",").append("\"").append(country).append("\"");
            isFirst = false;
        }
        builder.append("]");
        return builder.toString();
    }

    AvailabilityCategory getPrimaryCategory();
    String getImageURL();

    @Override
    default Object getJSONData(Folder folder, String fileName, String countryBackendID) {
        return getJSONArray(folder, fileName, new CompletionHandler() {
            @Override
            public JSONArray loadJSONArray() {
                try {
                    final String string = loadData();
                    return string != null && string.startsWith("[") && string.endsWith("]") ? new JSONArray(string) : null;
                } catch (Exception e) {
                    WLUtilities.saveException(e);
                    return null;
                }
            }
        });
    }

    default CountryAvailability getAvailability(String countryBackendID) {
        final SovereignStateInfo info = getInfo();
        final boolean isTrue;
        if(AVAILABILITY_VALUES.containsKey(info)) {
            isTrue = isTrue(countryBackendID, AVAILABILITY_VALUES.get(info));
        } else {
            final Object obj = getJSONData(getFolder(), info.getTitle(), countryBackendID);
            if(obj != null) {
                final JSONArray array = (JSONArray) obj;
                AVAILABILITY_VALUES.put(info, array);
                isTrue = isTrue(countryBackendID, array);
            } else {
                return null;
            }
        }
        return getAvailability(isTrue);
    }

    default boolean isTrue(String countryBackendID, JSONArray array) {
        for(Object obj : array) {
            final String string = (String) obj;
            if(countryBackendID.equals(string)) {
                return true;
            }
        }
        return false;
    }
}
