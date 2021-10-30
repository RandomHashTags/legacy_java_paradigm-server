package me.randomhashtags.worldlaws.country.subdivisions;

import me.randomhashtags.worldlaws.country.SovereignStateSubdivision;
import me.randomhashtags.worldlaws.country.WLCountry;

public enum SubdivisionsFiji implements SovereignStateSubdivision { // https://en.wikipedia.org/wiki/Provinces_of_Fiji
    BA,
    BUA,
    CAKAUDROVE,
    KADAVU,
    LAU,
    LOMAIVITI,
    MACUATA,
    NADROGA_NAVOSA,
    NAITASIRI,
    NAMOSI,
    RA,
    REWA,
    SERUA,
    TAILEVU,
    ;

    @Override
    public WLCountry getCountry() {
        return WLCountry.FIJI;
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
