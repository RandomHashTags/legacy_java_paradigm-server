package me.randomhashtags.worldlaws.country.subdivisions;

import me.randomhashtags.worldlaws.country.SovereignStateSubdivision;
import me.randomhashtags.worldlaws.country.WLCountry;

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
