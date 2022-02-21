package me.randomhashtags.worldlaws.country.subdivisions.t;

import me.randomhashtags.worldlaws.country.SovereignStateSubdivision;
import me.randomhashtags.worldlaws.country.WLCountry;
import me.randomhashtags.worldlaws.country.subdivisions.SubdivisionType;

public enum SubdivisionsTonga implements SovereignStateSubdivision { // https://en.wikipedia.org/wiki/Administrative_divisions_of_Tonga
    EUA,
    HA_APAI,
    NIUA_ISLANDS,
    TONGATAPU,
    VAVA_U,
    ;

    @Override
    public WLCountry getCountry() {
        return WLCountry.TONGA;
    }

    @Override
    public SubdivisionType getDefaultType() {
        return SubdivisionType.DISTRICTS;
    }

    @Override
    public String getRealName() {
        switch (this) {
            case EUA: return "'Eua";
            case HA_APAI: return "Ha'apai";
            case VAVA_U: return "Vava'u";
            default: return null;
        }
    }

    @Override
    public String getWikipediaURLSuffix(String suffix) {
        return "";
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
