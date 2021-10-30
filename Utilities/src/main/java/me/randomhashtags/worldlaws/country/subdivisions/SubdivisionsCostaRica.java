package me.randomhashtags.worldlaws.country.subdivisions;

import me.randomhashtags.worldlaws.country.SovereignStateSubdivision;
import me.randomhashtags.worldlaws.country.WLCountry;

public enum SubdivisionsCostaRica implements SovereignStateSubdivision { // https://en.wikipedia.org/wiki/Provinces_of_Costa_Rica
    ALAJUELA,
    CARTAGO,
    GUANACASTE,
    HEREDIA,
    LIMON,
    PUNTARENAS,
    SAN_JOSE,
    ;

    @Override
    public WLCountry getCountry() {
        return WLCountry.COSTA_RICA;
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
