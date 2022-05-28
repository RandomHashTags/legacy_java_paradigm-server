package me.randomhashtags.worldlaws.country.subdivisions.n;

import me.randomhashtags.worldlaws.country.SovereignStateSubdivision;
import me.randomhashtags.worldlaws.country.WLCountry;
import me.randomhashtags.worldlaws.country.subdivisions.SubdivisionType;

public enum SubdivisionsNepal implements SovereignStateSubdivision { // https://en.wikipedia.org/wiki/Provinces_of_Nepal
    BAGMATI,
    GANDAKI,
    KARNALI,
    LUMBINI,
    MADHESH,
    PROVINCE_1,
    SUDURPASHCHIM,
    ;

    @Override
    public WLCountry getCountry() {
        return WLCountry.NEPAL;
    }

    @Override
    public SubdivisionType getDefaultType() {
        return SubdivisionType.PROVINCES;
    }

    @Override
    public String getRealName() {
        switch (this) {
            case PROVINCE_1: return "Province No. 1";
            default: return null;
        }
    }

    @Override
    public String getWikipediaURLSuffix(String suffix) {
        switch (this) {
            case MADHESH:
            case PROVINCE_1:
                return "";
            default:
                return null;
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
