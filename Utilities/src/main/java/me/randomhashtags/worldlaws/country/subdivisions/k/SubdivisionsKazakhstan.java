package me.randomhashtags.worldlaws.country.subdivisions.k;

import me.randomhashtags.worldlaws.country.SovereignStateSubdivision;
import me.randomhashtags.worldlaws.country.WLCountry;
import me.randomhashtags.worldlaws.country.subdivisions.SubdivisionType;

public enum SubdivisionsKazakhstan implements SovereignStateSubdivision { // https://en.wikipedia.org/wiki/Regions_of_Kazakhstan
    AKMOLA_REGION,
    AKTOBE_REGION,
    ALMATY,
    ALMATY_REGION,
    ATYRAU_REGION,
    BAIKONUR,
    EAST_KAZAKHSTAN_REGION,
    JAMBYL_REGION,
    KARAGANDA_REGION,
    KOSTANAY_REGION,
    KYZYLORDA_REGION,
    MANGYSTAU_REGION,
    NORTH_KAZAKHSTAN_REGION,
    NUR_SULTAN,
    PAVLODAR_REGION,
    SHUMKENT,
    TURKISTAN_REGION,
    WEST_KAZAKHSTAN_REGION,
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
