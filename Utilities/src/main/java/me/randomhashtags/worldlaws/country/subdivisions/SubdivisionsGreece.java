package me.randomhashtags.worldlaws.country.subdivisions;

import me.randomhashtags.worldlaws.country.SovereignStateSubdivision;
import me.randomhashtags.worldlaws.country.WLCountry;

public enum SubdivisionsGreece implements SovereignStateSubdivision { // https://en.wikipedia.org/wiki/Administrative_regions_of_Greece
    ATTICA,
    CENTRAL_GREECE,
    CENTRAL_MACEDONIA,
    CRETE,
    EASTERN_MACEDONIA_AND_THRACE,
    EPIRUS,
    IONIAN_ISLANDS,
    MOUNT_ATHOS,
    NORTH_AEGEAN,
    PELOPONNESE,
    SOUTH_AEGEAN,
    THESSALY,
    WESTERN_GREECE,
    WESTERN_MACEDONIA,
    ;

    @Override
    public WLCountry getCountry() {
        return WLCountry.GREECE;
    }

    @Override
    public String getPostalCodeAbbreviation() {
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
