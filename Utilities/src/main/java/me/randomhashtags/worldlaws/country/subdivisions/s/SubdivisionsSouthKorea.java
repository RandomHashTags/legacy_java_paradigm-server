package me.randomhashtags.worldlaws.country.subdivisions.s;

import me.randomhashtags.worldlaws.country.SovereignStateSubdivision;
import me.randomhashtags.worldlaws.country.WLCountry;

public enum SubdivisionsSouthKorea implements SovereignStateSubdivision { // https://en.wikipedia.org/wiki/Administrative_divisions_of_South_Korea
    BUSAN,

    NORTH_CHUNGCHEONG,
    SOUTH_CHUNGCHEONG,

    DAEGU,
    DAEJEON,
    GANGWON,
    GYEONGGI,
    GWANGJU,

    NORTH_GYEONGSANG,
    SOUTH_GYEONGSANG,

    INCHEON,
    JEJU,

    NORTH_JEOLLA,
    SOUTH_JEOLLA,

    SEJONG,
    ULSAN,
    ;

    @Override
    public WLCountry getCountry() {
        return WLCountry.SOUTH_KOREA;
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
