package me.randomhashtags.worldlaws.country.subdivisions.e;

import me.randomhashtags.worldlaws.country.SovereignStateSubdivision;
import me.randomhashtags.worldlaws.country.WLCountry;
import me.randomhashtags.worldlaws.country.subdivisions.SubdivisionType;

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

    ADDIS_ABABA,
    DIRE_DAWA,
    ;

    @Override
    public WLCountry getCountry() {
        return WLCountry.ETHIOPIA;
    }

    @Override
    public SubdivisionType getDefaultType() {
        return SubdivisionType.STATES;
    }

    @Override
    public SubdivisionType getType() {
        switch (this) {
            case ADDIS_ABABA:
            case DIRE_DAWA:
                return SubdivisionType.CHARTERED_CITIES;
            default: return null;
        }
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
