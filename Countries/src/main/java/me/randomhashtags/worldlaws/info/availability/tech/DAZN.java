package me.randomhashtags.worldlaws.info.availability.tech;

import me.randomhashtags.worldlaws.CompletionHandler;
import me.randomhashtags.worldlaws.info.availability.CountryAvailabilityCategory;
import me.randomhashtags.worldlaws.info.availability.CountryAvailabilityService;
import me.randomhashtags.worldlaws.location.CountryInfo;

import java.util.HashMap;

public enum DAZN implements CountryAvailabilityService {
    INSTANCE;

    private HashMap<String, String> countries;

    @Override
    public CountryInfo getInfo() {
        return CountryInfo.AVAILABILITY_DAZN;
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
        // https://www.dazn.com/en-DE/help/articles/where-is-dazn-available
        countries = new HashMap<>();
        final String value = getAvailability(true).toString();
        final String[] array = {
                "austria",
                "brazil",
                "canada",
                "germany",
                "italy",
                "japan",
                "spain",
                "switzerland",
                "unitedstates",
        };
        for(String country : array) {
            countries.put(country, value);
        }
        handler.handle(null);
    }
}
