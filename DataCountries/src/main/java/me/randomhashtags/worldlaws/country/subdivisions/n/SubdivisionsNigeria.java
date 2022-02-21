package me.randomhashtags.worldlaws.country.subdivisions.n;

import me.randomhashtags.worldlaws.country.SovereignStateSubdivision;
import me.randomhashtags.worldlaws.country.WLCountry;
import me.randomhashtags.worldlaws.country.subdivisions.SubdivisionType;

public enum SubdivisionsNigeria implements SovereignStateSubdivision { // https://en.wikipedia.org/wiki/States_of_Nigeria
    ABIA,
    ADAMAWA,
    AKWA_IBOM,
    ANAMBRA,
    BAUCHI,
    BAYELSA,
    BENUE,
    BORNO,
    CROSS_RIVER,
    DELTA,
    EBONYI,
    EDO,
    EKITI,
    ENUGU,
    GOMBE,
    IMO,
    JIGAWA,
    KADUNA,
    KANO,
    KATSINA,
    KEBBI,
    KOGI,
    KWARA,
    LAGOS,
    NASARAWA,
    NIGER,
    OGUN,
    ONDO,
    OSUN,
    OYO,
    PLATEAU,
    RIVERS,
    SOKOTO,
    TARABA,
    YOBE,
    ZAMFARA,

    FEDERAL_CAPITAL_TERRITORY,
    ;

    @Override
    public WLCountry getCountry() {
        return WLCountry.NIGERIA;
    }

    @Override
    public SubdivisionType getDefaultType() {
        return SubdivisionType.STATES;
    }

    @Override
    public String getConditionalName() {
        switch (this) {
            case FEDERAL_CAPITAL_TERRITORY: return "Federal Capital Territory";
            default: return SovereignStateSubdivision.super.getConditionalName();
        }
    }

    @Override
    public SubdivisionType getType() {
        switch (this) {
            case FEDERAL_CAPITAL_TERRITORY: return SubdivisionType.FEDERAL_TERRITORIES;
            default: return null;
        }
    }

    @Override
    public String getWikipediaURLSuffix(String suffix) {
        switch (this) {
            case FEDERAL_CAPITAL_TERRITORY:
                return "(Nigeria)";
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
