package me.randomhashtags.worldlaws.info.availability.tech;

import me.randomhashtags.worldlaws.CompletionHandler;
import me.randomhashtags.worldlaws.info.availability.CountryAvailability;
import me.randomhashtags.worldlaws.info.availability.CountryAvailabilityCategory;
import me.randomhashtags.worldlaws.info.availability.CountryAvailabilityService;
import me.randomhashtags.worldlaws.location.CountryInfo;

import java.util.HashMap;

public enum ATandTTV implements CountryAvailabilityService {
    INSTANCE;

    private HashMap<String, String> countries;

    @Override
    public CountryInfo getInfo() {
        return CountryInfo.AVAILABILITY_AT_AND_T_TV;
    }

    @Override
    public CountryAvailabilityCategory getCategory() {
        return CountryAvailabilityCategory.ENTERTAINMENT_STREAMING;
    }

    @Override
    public HashMap<String, String> getCountries() {
        return countries;
    }

    @Override
    public void refresh(CompletionHandler handler) {
        // https://en.wikipedia.org/wiki/AT%26T_TV
        countries = new HashMap<>();
        final String value = getAvailability(true).toString();
        final String[] values = {
                "unitedstates"
        };
        for(String country : values) {
            countries.put(country, value);
        }
        handler.handle(null);
    }
}
