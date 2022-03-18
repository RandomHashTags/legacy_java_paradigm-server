package me.randomhashtags.worldlaws;

import me.randomhashtags.worldlaws.country.WLCountry;

public final class CustomCountry implements Country {

    private final WLCountry wlCountryCache;

    public CustomCountry(WLCountry country) {
        wlCountryCache = country;
    }

    @Override
    public WLCountry getWLCountry() {
        return wlCountryCache;
    }

    @Override
    public String getFlagURL() {
        return null;
    }
}
