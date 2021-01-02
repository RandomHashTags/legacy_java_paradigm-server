package me.randomhashtags.worldlaws.service;

import me.randomhashtags.worldlaws.location.CountryInfo;

import java.util.ArrayList;
import java.util.List;

public abstract class CountryServices {
    public static final List<CountryService> SERVICES = new ArrayList<>();

    public static CountryService valueOfCountryInfo(String countryInfo) {
        for(CountryService service : SERVICES) {
            final CountryInfo info = service.getInfo();
            if(info != null && info.name().equalsIgnoreCase(countryInfo)) {
                return service;
            }
        }
        return null;
    }
}
