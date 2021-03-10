package me.randomhashtags.worldlaws.info.availability.tech;

import me.randomhashtags.worldlaws.CompletionHandler;
import me.randomhashtags.worldlaws.info.availability.CountryAvailabilityCategory;
import me.randomhashtags.worldlaws.info.availability.CountryAvailabilityService;
import me.randomhashtags.worldlaws.location.CountryInfo;

import java.util.HashMap;

public enum PlayStationNow implements CountryAvailabilityService {
    INSTANCE;

    private HashMap<String, String> countries;

    @Override
    public CountryInfo getInfo() {
        return CountryInfo.AVAILABILITY_PLAYSTATION_NOW;
    }

    @Override
    public CountryAvailabilityCategory getCategory() {
        return CountryAvailabilityCategory.ENTERTAINMENT_GAMING;
    }

    @Override
    public HashMap<String, String> getCountries() {
        return countries;
    }

    @Override
    public void refresh(CompletionHandler handler) {
        // https://en.wikipedia.org/wiki/PlayStation_Now
        // https://www.playstation.com/en-us/support/subscriptions/playstation-now-support/
        countries = new HashMap<>();
        final String value = getAvailability(true).toString();
        final String[] array = {
                "austria",
                "belgium",
                "canada",
                "denmark",
                "finland",
                "france",
                "germany",
                "ireland",
                "italy",
                "japan",
                "luxembourg",
                "netherlands",
                "norway",
                "portugal",
                "spain",
                "sweden",
                "switzerland",
                "unitedkingdom",
                "unitedstates",
        };
        for(String country : array) {
            countries.put(country, value);
        }
        handler.handle(null);
    }
}
