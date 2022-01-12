package me.randomhashtags.worldlaws.info.availability;

import me.randomhashtags.worldlaws.CompletionHandler;
import me.randomhashtags.worldlaws.Folder;
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
    default void getJSONData(Folder folder, String fileName, String countryBackendID, CompletionHandler handler) {
        getJSONArray(folder, fileName, new CompletionHandler() {
            @Override
            public void load(CompletionHandler handler) {
                final JSONArray array = new JSONArray(loadData());
                handler.handleJSONArray(array);
            }

            @Override
            public void handleJSONArray(JSONArray array) {
                handler.handleJSONArray(array);
            }
        });
    }

    @Override
    default void getCountryValue(String countryBackendID, CompletionHandler handler) {
        final SovereignStateInfo info = getInfo();
        if(AVAILABILITY_VALUES.containsKey(info)) {
            final boolean isTrue = isTrue(countryBackendID, AVAILABILITY_VALUES.get(info));
            final CountryAvailability value = getAvailability(isTrue);
            handler.handleObject(value);
        } else {
            getJSONData(getFolder(), info.getTitle(), countryBackendID, new CompletionHandler() {
                @Override
                public void handleJSONArray(JSONArray array) {
                    AVAILABILITY_VALUES.put(info, array);
                    final boolean isTrue = isTrue(countryBackendID, array);
                    final CountryAvailability value = getAvailability(isTrue);
                    handler.handleObject(value);
                }
            });
        }
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
