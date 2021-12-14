package me.randomhashtags.worldlaws.country.subdivisions.m;

import me.randomhashtags.worldlaws.country.SovereignStateSubdivision;
import me.randomhashtags.worldlaws.country.WLCountry;

public enum SubdivisionsMalaysia implements SovereignStateSubdivision { // https://en.wikipedia.org/wiki/States_and_federal_territories_of_Malaysia
    JOHOR,
    KEDAH,
    KELANTAN,
    MALACCA,
    NEGERI_SEMBILAN,
    PAHANG,
    PENANG,
    PERAK,
    PERLIS,
    SABAH,
    SARAWAK,
    SELANGOR,
    TERENGGANU,

    KUALA_LUMPUR,
    LABUAN,
    PUTRAJAYA,
    ;

    @Override
    public WLCountry getCountry() {
        return WLCountry.MALAYSIA;
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
