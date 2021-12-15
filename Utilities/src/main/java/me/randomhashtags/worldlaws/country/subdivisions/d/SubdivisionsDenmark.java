package me.randomhashtags.worldlaws.country.subdivisions.d;

import me.randomhashtags.worldlaws.country.SovereignStateSubdivision;
import me.randomhashtags.worldlaws.country.WLCountry;

public enum SubdivisionsDenmark implements SovereignStateSubdivision { // https://en.wikipedia.org/wiki/Region_of_Southern_Denmark
    HOVEDSTADEN,
    MIDTJYLLAND,
    NORDJYLLAND,
    SJAELLAND,
    SYDDANMARK,
    ;

    @Override
    public WLCountry getCountry() {
        return WLCountry.DENMARK;
    }

    @Override
    public String getISOAlpha2() {
        switch (this) {
            default: return null;
        }
    }

    @Override
    public String getFlagURL() {
        return null;
    }

    @Override
    public String getGovernmentWebsite() {
        return null;
    }
}
