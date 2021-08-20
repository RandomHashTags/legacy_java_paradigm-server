package me.randomhashtags.worldlaws.country.subdivisions;

import me.randomhashtags.worldlaws.CompletionHandler;
import me.randomhashtags.worldlaws.country.SovereignStateSubdivision;
import me.randomhashtags.worldlaws.country.WLCountry;

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
    public WLCountry getCountry() {
        return WLCountry.NEW_ZEALAND;
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

    @Override
    public void getCitiesHashSet(CompletionHandler handler) {
    }
}
