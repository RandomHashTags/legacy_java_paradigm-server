package me.randomhashtags.worldlaws.location.subdivisions;

import me.randomhashtags.worldlaws.location.SovereignStateSubdivision;

public enum NewZealandSubdivisions implements SovereignStateSubdivision {
    AUCKLAND,
    BAY_OF_PLENTY,
    CANTERBURY,
    GISBORNE,
    HAWKES_BAY, // TODO: add support for custom letters and punctuation
    MANAWATU_WHANGANUI,
    MARLBOROUGH,
    NELSON,
    NORTHLAND,
    OTAGO,
    SOUTHLAND,
    TARANAKI,
    TASMAN,
    WAIKATO,
    WELLINGTON,
    WEST_COAST
    ;

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
    public String getGovernmentURL() {
        return null;
    }
}
