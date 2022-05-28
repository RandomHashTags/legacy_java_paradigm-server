package me.randomhashtags.worldlaws.country.subdivisions.n;

import me.randomhashtags.worldlaws.country.SovereignStateSubdivision;
import me.randomhashtags.worldlaws.country.WLCountry;
import me.randomhashtags.worldlaws.country.subdivisions.SubdivisionType;

public enum SubdivisionsNewZealand implements SovereignStateSubdivision { // https://en.wikipedia.org/wiki/Regions_of_New_Zealand
    AUCKLAND,
    BAY_OF_PLENTY,
    CANTERBURY,
    GISBORNE,
    HAWKES_BAY,
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
    public SubdivisionType getDefaultType() {
        return SubdivisionType.REGIONS;
    }

    @Override
    public String getRealName() {
        switch (this) {
            case HAWKES_BAY: return "Hawke's Bay";
            case MANAWATU_WHANGANUI: return "Manawatū-Whanganui";
            default: return null;
        }
    }

    @Override
    public String getWikipediaURLSuffix(String suffix) {
        switch (this) {
            case BAY_OF_PLENTY:
            case MANAWATU_WHANGANUI:
            case OTAGO:
            case TARANAKI:
            case WAIKATO:
                return "";
            case CANTERBURY:
            case NELSON:
            case SOUTHLAND:
            case WEST_COAST:
                return ",_New_Zealand";
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
}
