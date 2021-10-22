package me.randomhashtags.worldlaws.country.subdivisions;

import me.randomhashtags.worldlaws.country.SovereignStateSubdivision;
import me.randomhashtags.worldlaws.country.WLCountry;

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
    public String getPostalCodeAbbreviation() {
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
