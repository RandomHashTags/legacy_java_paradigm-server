package me.randomhashtags.worldlaws.country.subdivisions;

import me.randomhashtags.worldlaws.country.SovereignStateSubdivision;
import me.randomhashtags.worldlaws.country.WLCountry;

public enum SubdivisionsChile implements SovereignStateSubdivision { // https://en.wikipedia.org/wiki/Regions_of_Chile
    ANTOFAGASTA,
    ARAUCANIA,
    ARICA_AND_PARINACOTA,
    ATACAMA,
    AYSEN,
    BIOBIO,
    COQUIMBO,
    LOS_LAGOS,
    LOS_RIOS,
    MAGALLANES,
    MAULE,
    METROPOLITAN,
    NUBLE,
    O_HIGGINES,
    TARAPACA,
    VALPARAISO,
    ;

    @Override
    public WLCountry getCountry() {
        return WLCountry.CHILE;
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
