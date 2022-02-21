package me.randomhashtags.worldlaws.country.subdivisions.s;

import me.randomhashtags.worldlaws.country.SovereignStateSubdivision;
import me.randomhashtags.worldlaws.country.WLCountry;
import me.randomhashtags.worldlaws.country.subdivisions.SubdivisionType;

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
    SEOUL,
    ULSAN,
    ;

    @Override
    public WLCountry getCountry() {
        return WLCountry.SOUTH_KOREA;
    }

    @Override
    public SubdivisionType getDefaultType() {
        return SubdivisionType.PROVINCES;
    }

    @Override
    public SubdivisionType getType() {
        switch (this) {
            case BUSAN:
            case DAEGU:
            case DAEJEON:
            case GWANGJU:
            case INCHEON:
            case ULSAN:
                return SubdivisionType.METROPOLITAN_CITIES;
            case SEJONG:
                return SubdivisionType.SPECIAL_SELF_GOVERNING_CITIES;
            case SEOUL:
                return SubdivisionType.SPECIAL_CITIES;
            default:
                return null;
        }
    }

    @Override
    public String getWikipediaURLSuffix(String suffix) {
        switch (this) {
            case BUSAN:
            case DAEGU:
            case DAEJEON:
            case GWANGJU:
            case INCHEON:
            case SEOUL:
            case ULSAN:
                return "";
            case GANGWON:
                return suffix + ",_South_Korea";
            case SEJONG:
                return "City";
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

    @Override
    public String getGovernmentWebsite() {
        return null;
    }
}
