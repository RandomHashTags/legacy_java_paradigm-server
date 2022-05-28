package me.randomhashtags.worldlaws.country.subdivisions.c;

import me.randomhashtags.worldlaws.country.SovereignStateSubdivision;
import me.randomhashtags.worldlaws.country.WLCountry;
import me.randomhashtags.worldlaws.country.subdivisions.SubdivisionType;

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
    public SubdivisionType getDefaultType() {
        return SubdivisionType.PROVINCES;
    }

    @Override
    public String getRealName() {
        switch (this) {
            case LIMON: return "Limón";
            case SAN_JOSE: return "San José";
            default: return null;
        }
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
