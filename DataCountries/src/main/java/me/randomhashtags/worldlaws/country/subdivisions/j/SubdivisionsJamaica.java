package me.randomhashtags.worldlaws.country.subdivisions.j;

import me.randomhashtags.worldlaws.country.SovereignStateSubdivision;
import me.randomhashtags.worldlaws.country.WLCountry;
import me.randomhashtags.worldlaws.country.subdivisions.SubdivisionType;

public enum SubdivisionsJamaica implements SovereignStateSubdivision { // https://en.wikipedia.org/wiki/Parishes_of_Jamaica
    HANOVER,
    SAINT_ELIZABETH,
    SAINT_JAMES,
    TRELAWNY,
    WESTMORELAND,
    CLARENDON,
    MANCHESTER,
    SAINT_ANN,
    SAINT_CATHERINE,
    SAINT_MARY,
    KINGSTON,
    PORTLAND,
    SAINT_ANDREW,
    SAINT_THOMAS,
    ;

    @Override
    public WLCountry getCountry() {
        return WLCountry.JAMAICA;
    }

    @Override
    public SubdivisionType getDefaultType() {
        return SubdivisionType.PARISHES;
    }

    @Override
    public String getWikipediaURLSuffix(String suffix) {
        switch (this) {
            case SAINT_JAMES:
            case CLARENDON:
            case SAINT_ANDREW:
            case SAINT_MARY:
            case SAINT_THOMAS:
                return suffix + ",_Jamaica";
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
