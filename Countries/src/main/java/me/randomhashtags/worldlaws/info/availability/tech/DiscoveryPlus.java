package me.randomhashtags.worldlaws.info.availability.tech;

import me.randomhashtags.worldlaws.CompletionHandler;
import me.randomhashtags.worldlaws.WLLogger;
import me.randomhashtags.worldlaws.info.availability.CountryAvailability;
import me.randomhashtags.worldlaws.info.availability.CountryAvailabilityCategory;
import me.randomhashtags.worldlaws.location.CountryInfo;
import me.randomhashtags.worldlaws.service.CountryService;

import java.util.HashMap;
import java.util.logging.Level;

public enum DiscoveryPlus implements CountryService {
    INSTANCE;

    private HashMap<String, String> availabilities;

    @Override
    public CountryInfo getInfo() {
        return CountryInfo.AVAILABILITY_DISCOVERY_PLUS;
    }

    @Override
    public void getValue(String countryBackendID, CompletionHandler handler) {
        if(availabilities != null) {
            handler.handle(getValue(countryBackendID));
        } else {
            refresh(new CompletionHandler() {
                @Override
                public void handle(Object object) {
                    handler.handle(getValue(countryBackendID));
                }
            });
        }
    }

    private String getValue(String countryBackendID) {
        if(!availabilities.containsKey(countryBackendID)) {
            availabilities.put(countryBackendID, new CountryAvailability(getInfo().getTitle(), false, CountryAvailabilityCategory.ENTERTAINMENT_STREAMING).toString());
        }
        return availabilities.get(countryBackendID);
    }

    private void refresh(CompletionHandler handler) {
        // https://www.discoveryplus.com
        final long started = System.currentTimeMillis();
        availabilities = new HashMap<>();
        final String title = getInfo().getTitle(), value = new CountryAvailability(title, true, CountryAvailabilityCategory.ENTERTAINMENT_STREAMING).toString();
        final String[] countries = {
                "unitedstates"
        };
        for(String country : countries) {
            availabilities.put(country, value);
        }
        WLLogger.log(Level.INFO, "DiscoveryPlus - refreshed (took " + (System.currentTimeMillis()-started) + "ms)");
        handler.handle(null);
    }
}
