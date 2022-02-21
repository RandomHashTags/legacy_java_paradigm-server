package me.randomhashtags.worldlaws.country.subdivisions.k;

import me.randomhashtags.worldlaws.country.SovereignStateSubdivision;
import me.randomhashtags.worldlaws.country.WLCountry;
import me.randomhashtags.worldlaws.country.subdivisions.SubdivisionType;

public enum SubdivisionsKazakhstan implements SovereignStateSubdivision { // https://en.wikipedia.org/wiki/Regions_of_Kazakhstan
    AKMOLA,
    AKTOBE,
    ALMATY,
    ALMATY_REGION,
    ATYRAU,
    BAIKONUR,
    EAST_KAZAKHSTAN,
    JAMBYL,
    KARAGANDA,
    KOSTANAY,
    KYZYLORDA,
    MANGYSTAU,
    NORTH_KAZAKHSTAN,
    NUR_SULTAN,
    PAVLODAR,
    SHYMKENT,
    TURKISTAN,
    WEST_KAZAKHSTAN,
    ;

    @Override
    public WLCountry getCountry() {
        return WLCountry.KAZAKHSTAN;
    }

    @Override
    public SubdivisionType getDefaultType() {
        return SubdivisionType.REGIONS;
    }

    @Override
    public String getRealName() {
        switch (this) {
            case NUR_SULTAN: return "Nur-Sultan";
            default: return null;
        }
    }

    @Override
    public String getWikipediaURLSuffix(String suffix) {
        switch (this) {
            case ALMATY:
            case ALMATY_REGION:
            case BAIKONUR:
            case NUR_SULTAN:
            case SHYMKENT:
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

    @Override
    public String getGovernmentWebsite() {
        return null;
    }
}
