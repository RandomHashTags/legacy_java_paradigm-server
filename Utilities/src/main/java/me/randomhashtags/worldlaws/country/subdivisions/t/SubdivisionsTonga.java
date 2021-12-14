package me.randomhashtags.worldlaws.country.subdivisions.t;

import me.randomhashtags.worldlaws.country.SovereignStateSubdivision;
import me.randomhashtags.worldlaws.country.WLCountry;

public enum SubdivisionsTonga implements SovereignStateSubdivision { // https://en.wikipedia.org/wiki/Administrative_divisions_of_Tonga
    EUA,
    HA_APAI,
    ONGO_NIUA,
    TONGATAPU,
    VAVA_U,
    ;

    @Override
    public WLCountry getCountry() {
        return WLCountry.TONGA;
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
