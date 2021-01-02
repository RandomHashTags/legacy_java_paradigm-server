package me.randomhashtags.worldlaws.info.availability.tech;

import me.randomhashtags.worldlaws.CompletionHandler;
import me.randomhashtags.worldlaws.WLLogger;
import me.randomhashtags.worldlaws.info.availability.CountryAvailability;
import me.randomhashtags.worldlaws.info.availability.CountryAvailabilityCategory;
import me.randomhashtags.worldlaws.location.CountryInfo;
import me.randomhashtags.worldlaws.service.CountryService;

import java.util.HashMap;
import java.util.logging.Level;

public enum Stadia implements CountryService {
    INSTANCE;

    private HashMap<String, String> availabilities;

    @Override
    public CountryInfo getInfo() {
        return CountryInfo.AVAILABILITY_STADIA;
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
            availabilities.put(countryBackendID, new CountryAvailability(getInfo().getTitle(), false, CountryAvailabilityCategory.ENTERTAINMENT_GAMING).toString());
        }
        return availabilities.get(countryBackendID);
    }

    private void refresh(CompletionHandler handler) {
        // https://support.google.com/stadia/answer/9566513?hl=en
        // https://en.wikipedia.org/wiki/Google_Stadia
        final long started = System.currentTimeMillis();
        availabilities = new HashMap<>();
        final String title = getInfo().getTitle();
        final String value = new CountryAvailability(title, true, CountryAvailabilityCategory.ENTERTAINMENT_GAMING).toString();
        availabilities.put("austria", value);
        availabilities.put("belgium", value);
        availabilities.put("canada", value);
        availabilities.put("czechrepublic", value);
        availabilities.put("denmark", value);
        availabilities.put("finland", value);
        availabilities.put("france", value);
        availabilities.put("germany", value);
        availabilities.put("hungary", value);
        availabilities.put("ireland", value);
        availabilities.put("italy", value);
        availabilities.put("netherlands", value);
        availabilities.put("norway", value);
        availabilities.put("romania", value);
        availabilities.put("poland", value);
        availabilities.put("portugal", value);
        availabilities.put("slovakia", value);
        availabilities.put("spain", value);
        availabilities.put("sweden", value);
        availabilities.put("switzerland", value);
        availabilities.put("unitedkingdom", value);
        availabilities.put("unitedstates", value);
        WLLogger.log(Level.INFO, "Stadia - refreshed (took " + (System.currentTimeMillis()-started) + "ms)");
        handler.handle(null);
    }
}
