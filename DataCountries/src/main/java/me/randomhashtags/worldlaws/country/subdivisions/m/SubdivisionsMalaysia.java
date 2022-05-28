package me.randomhashtags.worldlaws.country.subdivisions.m;

import me.randomhashtags.worldlaws.country.SovereignStateSubdivision;
import me.randomhashtags.worldlaws.country.WLCountry;
import me.randomhashtags.worldlaws.country.subdivisions.SubdivisionType;

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
    public SubdivisionType getDefaultType() {
        return SubdivisionType.STATES;
    }

    @Override
    public SubdivisionType getType() {
        switch (this) {
            case KUALA_LUMPUR:
            case LABUAN:
            case PUTRAJAYA:
                return SubdivisionType.FEDERAL_TERRITORIES;
            default: return null;
        }
    }

    @Override
    public String getWikipediaURLSuffix(String suffix) {
        switch (this) {
            default:
                return "";
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
