package me.randomhashtags.worldlaws.country.subdivisions;

import me.randomhashtags.worldlaws.country.SovereignStateSubdivision;
import me.randomhashtags.worldlaws.country.WLCountry;

public enum SubdivisionsTonga implements SovereignStateSubdivision { // https://en.wikipedia.org/wiki/Administrative_divisions_of_Tonga
    EUA, // 'Eua
    HA_APAI, // Ha'apai
    ONGO_NIUA,
    TONGATAPU,
    VAVA_U, // Vava'u
    ;

    @Override
    public WLCountry getCountry() {
        return WLCountry.TONGA;
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
