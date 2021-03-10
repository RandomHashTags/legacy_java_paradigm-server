package me.randomhashtags.worldlaws.info.availability;

import me.randomhashtags.worldlaws.FileType;
import me.randomhashtags.worldlaws.info.service.CountryService;
import org.jsoup.select.Elements;

import java.util.HashMap;

public interface CountryAvailabilityService extends CountryService {
    @Override
    default FileType getFileType() {
        return FileType.COUNTRIES_AVAILABILITIES;
    }

    default Elements getAvailabilityDocumentElements(String url, String targetElements) {
        return getAvailabilityDocumentElements(url, targetElements, -1);
    }
    default Elements getAvailabilityDocumentElements(String url, String targetElements, int index) {
        return getDocumentElements(FileType.COUNTRIES_AVAILABILITIES, url, targetElements, index);
    }

    CountryAvailabilityCategory getCategory();

    default CountryAvailability getAvailability(boolean value) {
        return new CountryAvailability(getInfo().getTitle(), value, getCategory());
    }

    @Override
    default String getValue(String countryBackendID) {
        final HashMap<String, String> countries = getCountries();
        if(!countries.containsKey(countryBackendID)) {
            final String value = getAvailability(false).toString();
            countries.put(countryBackendID, value);
        }
        return countries.get(countryBackendID);
    }
}
