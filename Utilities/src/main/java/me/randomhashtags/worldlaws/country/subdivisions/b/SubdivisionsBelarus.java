package me.randomhashtags.worldlaws.country.subdivisions.b;

import me.randomhashtags.worldlaws.country.SovereignStateSubdivision;
import me.randomhashtags.worldlaws.country.WLCountry;

public enum SubdivisionsBelarus implements SovereignStateSubdivision { // https://en.wikipedia.org/wiki/Regions_of_Belarus
    BREST,
    GOMEL,
    GRODNO,
    MINSK,
    MINSK_CITY,
    MOGILEV,
    VITESBSK,
    ;

    @Override
    public WLCountry getCountry() {
        return WLCountry.BELARUS;
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
