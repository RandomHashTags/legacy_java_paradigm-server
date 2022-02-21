package me.randomhashtags.worldlaws.country.subdivisions.l;

import me.randomhashtags.worldlaws.country.SovereignStateSubdivision;
import me.randomhashtags.worldlaws.country.WLCountry;
import me.randomhashtags.worldlaws.country.subdivisions.SubdivisionType;

public enum SubdivisionsLithuania implements SovereignStateSubdivision { // https://en.wikipedia.org/wiki/Counties_of_Lithuania
    ALYTUS,
    KAUNAS,
    KLAIPEDA,
    MARIJAMPOLE,
    PANEVEZYS,
    SIAULIAI,
    TAURAGE,
    TELSIAI,
    UTENA,
    VILNIUS,
    ;

    @Override
    public WLCountry getCountry() {
        return WLCountry.LITHUANIA;
    }

    @Override
    public SubdivisionType getDefaultType() {
        return SubdivisionType.COUNTIES;
    }

    @Override
    public String getRealName() {
        switch (this) {
            case KLAIPEDA: return "Klaipėda";
            case MARIJAMPOLE: return "Marijampolė";
            case PANEVEZYS: return "Panevėžys";
            case SIAULIAI: return "Šiauliai";
            case TAURAGE: return "Tauragė";
            case TELSIAI: return "Telšiai";
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
