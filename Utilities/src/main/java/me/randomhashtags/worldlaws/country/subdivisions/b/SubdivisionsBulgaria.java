package me.randomhashtags.worldlaws.country.subdivisions.b;

import me.randomhashtags.worldlaws.country.SovereignStateSubdivision;
import me.randomhashtags.worldlaws.country.WLCountry;

public enum SubdivisionsBulgaria implements SovereignStateSubdivision { // https://en.wikipedia.org/wiki/Provinces_of_Bulgaria
    BLAGOEVGRAD,
    BURGAS,
    DOBRICH,
    GABROVO,
    HASKOVO,
    KARDZHALI,
    KYUSTENDIL,
    LOVECH,
    MONTANA,
    PAZARDZHIK,
    PERNIK,
    PLEVEN,
    PLOVDIV,
    RAZGRAD,
    RUSE,
    SHUMEN,
    SILISTRA,
    SLIVEN,
    SMOLYAN,
    SOFIA,
    SOFIA_CITY,
    STARA_ZAGORA,
    TARGOVISHTE,
    VARNA,
    VELIKO_TARNOVO,
    VIDIN,
    VRATSA,
    YAMBOL,
    ;

    @Override
    public WLCountry getCountry() {
        return WLCountry.BULGARIA;
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
