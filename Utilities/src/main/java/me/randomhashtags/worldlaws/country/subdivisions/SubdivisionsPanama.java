package me.randomhashtags.worldlaws.country.subdivisions;

import me.randomhashtags.worldlaws.country.SovereignStateSubdivision;
import me.randomhashtags.worldlaws.country.WLCountry;

public enum SubdivisionsPanama implements SovereignStateSubdivision { // https://en.wikipedia.org/wiki/Provinces_of_Panama
    BORCAS_DEL_TORO,
    CHIRIQUI,
    COCLE,
    COLON,
    DARIEN,
    HERRERA,
    LOS_SANTOS,
    PANAMA,
    PANAMA_OESTE,
    VERAGUAS,

    EMBERA,
    GUNA_YALA,
    NASO_TJER_DI,
    NGABE_BUGLE,
    ;

    @Override
    public WLCountry getCountry() {
        return WLCountry.PANAMA;
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
