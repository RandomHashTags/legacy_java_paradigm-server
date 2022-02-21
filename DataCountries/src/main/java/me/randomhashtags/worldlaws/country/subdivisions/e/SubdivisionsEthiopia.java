package me.randomhashtags.worldlaws.country.subdivisions.e;

import me.randomhashtags.worldlaws.country.SovereignStateSubdivision;
import me.randomhashtags.worldlaws.country.WLCountry;
import me.randomhashtags.worldlaws.country.subdivisions.SubdivisionType;

public enum SubdivisionsEthiopia implements SovereignStateSubdivision { // https://en.wikipedia.org/wiki/Subdivisions_of_Ethiopia
    AFAR,
    AMHARA,
    BENISHANGUL_GUMUZ,
    GAMBELA,
    HARARI,
    OROMIA,
    SIDAMA,
    SOMALI,
    SOUTH_WEST,
    SOUTHERN_NATIONS_NATIONALITIES_AND_PEOPLES_REGION,
    TIGRAY,

    ADDIS_ABABA,
    DIRE_DAWA,
    ;

    @Override
    public WLCountry getCountry() {
        return WLCountry.ETHIOPIA;
    }

    @Override
    public SubdivisionType getDefaultType() {
        return SubdivisionType.STATES;
    }

    @Override
    public SubdivisionType getType() {
        switch (this) {
            case ADDIS_ABABA:
            case DIRE_DAWA:
                return SubdivisionType.CHARTERED_CITIES;
            default: return null;
        }
    }

    @Override
    public String getConditionalName() {
        switch (this) {
            case SOUTH_WEST: return "South West Ethiopia";
            default: return SovereignStateSubdivision.super.getConditionalName();
        }
    }

    @Override
    public String getRealName() {
        switch (this) {
            case BENISHANGUL_GUMUZ: return "Benishangul-Gumuz";
            case SOUTHERN_NATIONS_NATIONALITIES_AND_PEOPLES_REGION: return "Southern Nations, Nationalities, and Peoples'";
            default: return null;
        }
    }

    @Override
    public String getWikipediaURLSuffix(String suffix) {
        switch (this) {
            case OROMIA:
            case ADDIS_ABABA:
            case DIRE_DAWA:
                return "";
            default:
                return "Region";
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
