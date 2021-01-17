package me.randomhashtags.worldlaws.info.service;

import me.randomhashtags.worldlaws.location.CountryInfo;

import java.util.HashSet;

public abstract class CountryServices {
    public static final HashSet<CountryService> SERVICES = new HashSet<>();

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
