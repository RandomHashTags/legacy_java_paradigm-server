package me.randomhashtags.worldlaws.country.subdivisions.z;

import me.randomhashtags.worldlaws.country.SovereignStateSubdivision;
import me.randomhashtags.worldlaws.country.WLCountry;
import me.randomhashtags.worldlaws.country.subdivisions.SubdivisionType;

public enum SubdivisionsZambia implements SovereignStateSubdivision { // https://en.wikipedia.org/wiki/Provinces_of_Zambia
    CENTRAL,
    COPPERBELT,
    EASTERN,
    LUAPULA,
    LUSAKA,
    MUCHINGA,
    NORTH_WESTERN,
    NORTHERN,
    SOUTHERN,
    WESTERN,
    ;

    @Override
    public WLCountry getCountry() {
        return WLCountry.ZAMBIA;
    }

    @Override
    public SubdivisionType getDefaultType() {
        return SubdivisionType.PROVINCES;
    }

    @Override
    public String getConditionalName() {
        switch (this) {
            case NORTH_WESTERN: return "North-Western";
            default: return null;
        }
    }

    @Override
    public String getWikipediaURLSuffix(String suffix) {
        switch (this) {
            case CENTRAL:
            case EASTERN:
            case NORTH_WESTERN:
            case NORTHERN:
            case SOUTHERN:
            case WESTERN:
                return "_Province,_Zambia";
            default:
                return "_Province";
        }
    }

    @Override
    public String getISOAlpha2() {
        switch (this) {
            default: return null;
        }
    }

    @Override
    public String getFlagURLWikipediaSVGID() {
        switch (this) {
            default: return null;
        }
    }
}
