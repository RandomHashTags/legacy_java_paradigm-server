package me.randomhashtags.worldlaws.country.subdivisions.n;

import me.randomhashtags.worldlaws.country.SovereignStateSubdivision;
import me.randomhashtags.worldlaws.country.WLCountry;
import me.randomhashtags.worldlaws.country.subdivisions.SubdivisionType;

public enum SubdivisionsNamibia implements SovereignStateSubdivision { // https://en.wikipedia.org/wiki/Regions_of_Namibia
    ERONGO,
    HARDAP,
    KAVANGO_EAST,
    KAVANGO_WEST,
    KHOMAS,
    KUNENE,
    OHANGWENA,
    OMAHEKE,
    OMUSATI,
    OSHANA,
    OSHIKOTO,
    OTJOZONDJUPA,
    ZAMBEZI,
    KARAS,
    ;

    @Override
    public WLCountry getCountry() {
        return WLCountry.NAMIBIA;
    }

    @Override
    public SubdivisionType getDefaultType() {
        return SubdivisionType.REGIONS;
    }

    @Override
    public String getRealName() {
        switch (this) {
            case KARAS: return "ǁKaras";
            default: return null;
        }
    }

    @Override
    public String getWikipediaURLSuffix(String suffix) {
        switch (this) {
            case KAVANGO_EAST:
            case KAVANGO_WEST:
            case OSHANA:
                return null;
            default:
                return "_Region";
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
