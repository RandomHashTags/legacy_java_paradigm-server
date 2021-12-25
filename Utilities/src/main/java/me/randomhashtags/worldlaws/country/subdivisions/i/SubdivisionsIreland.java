package me.randomhashtags.worldlaws.country.subdivisions.i;

import me.randomhashtags.worldlaws.country.SovereignStateSubdivision;
import me.randomhashtags.worldlaws.country.WLCountry;
import me.randomhashtags.worldlaws.country.subdivisions.SubdivisionType;

public enum SubdivisionsIreland implements SovereignStateSubdivision { // https://en.wikipedia.org/wiki/Provinces_of_Ireland
    CONNACHT,
    LEINSTER,
    MUNSTER,
    ULSTER,
    ;

    @Override
    public WLCountry getCountry() {
        return WLCountry.IRELAND;
    }

    @Override
    public SubdivisionType getDefaultType() {
        return SubdivisionType.PROVINCES;
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
