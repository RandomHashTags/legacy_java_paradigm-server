package me.randomhashtags.worldlaws.country.subdivisions;

import me.randomhashtags.worldlaws.country.SovereignStateSubdivision;
import me.randomhashtags.worldlaws.country.WLCountry;

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
    KINGSTON_PARISH,
    PORTLAND,
    SAINT_ANDREW,
    SAINT_THOMAS,
    ;

    @Override
    public WLCountry getCountry() {
        return WLCountry.JAMAICA;
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
