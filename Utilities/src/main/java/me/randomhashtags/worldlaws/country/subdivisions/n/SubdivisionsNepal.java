package me.randomhashtags.worldlaws.country.subdivisions.n;

import me.randomhashtags.worldlaws.country.SovereignStateSubdivision;
import me.randomhashtags.worldlaws.country.WLCountry;

public enum SubdivisionsNepal implements SovereignStateSubdivision { // https://en.wikipedia.org/wiki/Provinces_of_Nepal
    BAGMATI,
    GANDAKI,
    KARNALI,
    LUMBINI,
    PROVINCE_1,
    PROVINCE_2,
    SUDURPASHCHIM,
    ;

    @Override
    public WLCountry getCountry() {
        return WLCountry.NEPAL;
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
