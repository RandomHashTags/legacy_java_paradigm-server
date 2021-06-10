package me.randomhashtags.worldlaws.info.availability;

import me.randomhashtags.worldlaws.CompletionHandler;
import me.randomhashtags.worldlaws.FileType;
import me.randomhashtags.worldlaws.info.service.CountryService;
import me.randomhashtags.worldlaws.location.CountryInfo;
import me.randomhashtags.worldlaws.location.CountryInformationType;
import org.json.JSONArray;
import org.jsoup.select.Elements;

import java.util.HashMap;

public interface CountryAvailabilityService extends CountryService {
    HashMap<CountryInfo, JSONArray> AVAILABILITY_VALUES = new HashMap<>();

    @Override
    default FileType getFileType() {
        return FileType.COUNTRIES_AVAILABILITIES;
    }

    @Override
    default CountryInformationType getInformationType() {
        return CountryInformationType.AVAILABILITIES;
    }

    default Elements getAvailabilityDocumentElements(String url, String targetElements) {
        return getAvailabilityDocumentElements(url, targetElements, -1);
    }
    default Elements getAvailabilityDocumentElements(String url, String targetElements, int index) {
        return getDocumentElements(FileType.COUNTRIES_AVAILABILITIES, url, targetElements, index);
    }

    default CountryAvailability getAvailability(boolean value) {
        return new CountryAvailability(getInfo().getTitle(), value);
    }

    @Override
    default void getJSONData(FileType fileType, String fileName, String countryBackendID, CompletionHandler handler) {
        getJSONArray(fileType, fileName, new CompletionHandler() {
            @Override
            public void load(CompletionHandler handler) {
                loadData(handler);
            }

            @Override
            public void handleJSONArray(JSONArray array) {
                handler.handleJSONArray(array);
            }
        });
    }

    @Override
    default void getCountryValue(String countryBackendID, CompletionHandler handler) {
        final CountryInfo info = getInfo();
        if(AVAILABILITY_VALUES.containsKey(info)) {
            final boolean isTrue = isTrue(countryBackendID, AVAILABILITY_VALUES.get(info));
            final String value = getAvailability(isTrue).toString();
            handler.handle(value);
        } else {
            getJSONData(getFileType(), info.getTitle(), countryBackendID, new CompletionHandler() {
                @Override
                public void handleJSONArray(JSONArray array) {
                    AVAILABILITY_VALUES.put(info, array);
                    final boolean isTrue = isTrue(countryBackendID, array);
                    final String value = getAvailability(isTrue).toString();
                    handler.handle(value);
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