package me.randomhashtags.worldlaws.info.availability.tech;

import me.randomhashtags.worldlaws.CompletionHandler;
import me.randomhashtags.worldlaws.WLLogger;
import me.randomhashtags.worldlaws.info.availability.CountryAvailability;
import me.randomhashtags.worldlaws.info.availability.CountryAvailabilityCategory;
import me.randomhashtags.worldlaws.location.CountryInfo;
import me.randomhashtags.worldlaws.service.CountryService;

import java.util.HashMap;
import java.util.logging.Level;

public enum NvidiaGeforceNOW implements CountryService {
    INSTANCE;

    private HashMap<String, String> availabilities;

    @Override
    public CountryInfo getInfo() {
        return CountryInfo.AVAILABILITY_NVIDIA_GEFORCE_NOW;
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
        // https://nvidia.custhelp.com/app/answers/detail/a_id/5023
        final long started = System.currentTimeMillis();
        availabilities = new HashMap<>();
        final String title = getInfo().getTitle();
        final String value = new CountryAvailability(title, true, CountryAvailabilityCategory.ENTERTAINMENT_GAMING).toString();
        availabilities.put("albania", value);
        availabilities.put("andorra", value);
        availabilities.put("anguilla", value);
        availabilities.put("austria", value);
        availabilities.put("bahamas", value);
        availabilities.put("barbados", value);
        availabilities.put("belgium", value);
        availabilities.put("bermuda", value);
        availabilities.put("bosniaandherzegovina", value);
        availabilities.put("britishvirginislands", value);
        availabilities.put("bulgaria", value);
        availabilities.put("canada", value);
        availabilities.put("canaryislands", value);
        availabilities.put("caymanislands", value);
        availabilities.put("croatia", value);
        availabilities.put("czechrepublic", value);
        availabilities.put("denmark", value);
        availabilities.put("dominica", value);
        availabilities.put("dominicanrepublic", value);
        availabilities.put("estonia", value);
        availabilities.put("faroeislands", value);
        availabilities.put("finland", value);
        availabilities.put("france", value);
        availabilities.put("germany", value);
        availabilities.put("gibraltar", value);
        availabilities.put("greece", value);
        availabilities.put("hungary", value);
        availabilities.put("iceland", value);
        availabilities.put("ireland", value);
        availabilities.put("israel", value);
        availabilities.put("italy", value);
        availabilities.put("jamaica", value);
        availabilities.put("latvia", value);
        availabilities.put("lichtenstein", value);
        availabilities.put("luxembourg", value);
        availabilities.put("macedonia", value);
        availabilities.put("malta", value);
        availabilities.put("mexico", value);
        availabilities.put("montenegro", value);
        availabilities.put("montserrat", value);
        availabilities.put("morocco", value);
        availabilities.put("netherlands", value);
        availabilities.put("norway", value);
        availabilities.put("poland", value);
        availabilities.put("portugal", value);
        availabilities.put("puertorico", value);
        availabilities.put("romania", value);
        availabilities.put("saintbarthelemy", value);
        availabilities.put("saintpierreandmiquelon", value);
        availabilities.put("serbia", value);
        availabilities.put("spain", value);
        availabilities.put("sweden", value);
        availabilities.put("switzerland", value);
        availabilities.put("tunisia", value);
        availabilities.put("turkey", value);
        availabilities.put("turksandcaicosislands", value);
        availabilities.put("u.s.virginislands", value);
        availabilities.put("unitedkingdom", value);
        availabilities.put("unitedstates", value);
        WLLogger.log(Level.INFO, "NvidiaGeforceNOW - refreshed (took " + (System.currentTimeMillis()-started) + "ms)");
        handler.handle(null);
    }
}
