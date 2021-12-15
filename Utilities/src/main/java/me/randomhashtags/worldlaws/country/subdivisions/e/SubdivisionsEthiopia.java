package me.randomhashtags.worldlaws.country.subdivisions.e;

import me.randomhashtags.worldlaws.country.SovereignStateSubdivision;
import me.randomhashtags.worldlaws.country.WLCountry;

public enum SubdivisionsEthiopia implements SovereignStateSubdivision { // https://en.wikipedia.org/wiki/Subdivisions_of_Ethiopia
    AFAR,
    AMHARA,
    BENISHANGUL_GUMUZ,
    GAMBELA,
    HARARI,
    OROMIA,
    SIDAMA,
    SOMALI,
    SOUTH_WEST,
    SNNPR,
    TIGRAY,
    ;

    @Override
    public WLCountry getCountry() {
        return WLCountry.ETHIOPIA;
    }

    @Override
    public String getRealName() {
        switch (this) {
            case BENISHANGUL_GUMUZ: return "Benishangul-Gumuz";
            case SNNPR: return "SNNPR";
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

    @Override
    public String getGovernmentWebsite() {
        return null;
    }
}
