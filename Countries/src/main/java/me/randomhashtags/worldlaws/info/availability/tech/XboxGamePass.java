package me.randomhashtags.worldlaws.info.availability.tech;

import me.randomhashtags.worldlaws.CompletionHandler;
import me.randomhashtags.worldlaws.info.availability.CountryAvailabilityCategory;
import me.randomhashtags.worldlaws.info.availability.CountryAvailabilityService;
import me.randomhashtags.worldlaws.location.CountryInfo;

import java.util.HashMap;

public enum XboxGamePass implements CountryAvailabilityService {
    INSTANCE;

    private HashMap<String, String> countries;

    @Override
    public CountryInfo getInfo() {
        return CountryInfo.AVAILABILITY_XBOX_GAME_PASS;
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
        // https://en.wikipedia.org/wiki/Xbox_Game_Pass
        countries = new HashMap<>();
        final String value = getAvailability(true).toString();
        final String[] array = {
                "argentina",
                "australia",
                "austria",
                "belgium",
                "brazil",
                "canada",
                "chile",
                "colombia",
                "czechrepublic",
                "denmark",
                "finland",
                "france",
                "germany",
                "greece",
                "hongkong",
                "hungary",
                "india",
                "ireland",
                "israel",
                "italy",
                "japan",
                "mexico",
                "netherlands",
                "newzealand",
                "norway",
                "poland",
                "portugal",
                "russia",
                "saudiarabia",
                "singapore",
                "slovakia",
                "southafrica",
                "southkorea",
                "spain",
                "sweden",
                "switzerland",
                "taiwan",
                "turkey",
                "unitedarabemirates",
                "unitedkingdom",
                "unitedstates",
        };
        for(String country : array) {
            countries.put(country, value);
        }
        handler.handle(null);
    }
}
