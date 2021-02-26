package me.randomhashtags.worldlaws.info.availability;

import me.randomhashtags.worldlaws.FileType;
import me.randomhashtags.worldlaws.info.service.CountryService;
import org.jsoup.select.Elements;

import java.util.HashMap;

public interface CountryAvailabilityService extends CountryService {
    default Elements getAvailabilityDocumentElements(String url, String targetElements) {
        return getAvailabilityDocumentElements(url, targetElements, -1);
    }
    default Elements getAvailabilityDocumentElements(String url, String targetElements, int index) {
        return getDocumentElements(FileType.AVAILABILITIES, url, targetElements, index);
    }

    CountryAvailabilityCategory getCategory();

    default String getAvailability(boolean value) {
        return new CountryAvailability(getInfo().getTitle(), value, getCategory()).toString();
    }

    @Override
    default String getValue(String countryBackendID) {
        final HashMap<String, String> countries = getCountries();
        if(!countries.containsKey(countryBackendID)) {
            final String value = getAvailability(false);
            countries.put(countryBackendID, value);
        }
        return countries.get(countryBackendID);
    }
}
