package me.randomhashtags.worldlaws.country.subdivisions.d;

import me.randomhashtags.worldlaws.country.SovereignStateSubdivision;
import me.randomhashtags.worldlaws.country.WLCountry;

public enum SubdivisionsDominica implements SovereignStateSubdivision { // https://en.wikipedia.org/wiki/Parishes_of_Dominica
    SAINT_ANDREW,
    SAINT_DAVID,
    SAINT_GEORGE,
    SAINT_JOHN,
    SAINT_JOSEPH,
    SAINT_LUKE,
    SAINT_MARK,
    SAINT_PATRICK,
    SAINT_PAUL,
    SAINT_PETER,
    ;

    @Override
    public WLCountry getCountry() {
        return WLCountry.DOMINICA;
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
