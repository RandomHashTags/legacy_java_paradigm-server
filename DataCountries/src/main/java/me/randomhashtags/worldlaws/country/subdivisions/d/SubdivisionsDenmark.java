package me.randomhashtags.worldlaws.country.subdivisions.d;

import me.randomhashtags.worldlaws.country.SovereignStateSubdivision;
import me.randomhashtags.worldlaws.country.WLCountry;
import me.randomhashtags.worldlaws.country.subdivisions.SubdivisionType;

public enum SubdivisionsDenmark implements SovereignStateSubdivision { // https://en.wikipedia.org/wiki/Regions_of_Denmark
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
    public SubdivisionType getDefaultType() {
        return SubdivisionType.REGIONS;
    }

    @Override
    public String getWikipediaURLPrefix() {
        return "Region_";
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
}
