package me.randomhashtags.worldlaws.country.subdivisions.n;

import me.randomhashtags.worldlaws.country.SovereignStateSubdivision;
import me.randomhashtags.worldlaws.country.WLCountry;
import me.randomhashtags.worldlaws.country.subdivisions.SubdivisionType;

public enum SubdivisionsNetherlands implements SovereignStateSubdivision { // https://en.wikipedia.org/wiki/Provinces_of_the_Netherlands
    DRENTHE,
    FLEVOLAND,
    FRIESLAND,
    GELDERLAND,
    GRONINGEN,
    LIMBURG,
    NORTH_BRABANT,
    NORTH_HOLLAND,
    OVERIJSSEL,
    SOUTH_HOLLAND,
    UTRECHT,
    ZEELAND,

    BONAIRE,
    SABA,
    SINT_EUSTATIUS,
    ;

    @Override
    public WLCountry getCountry() {
        return WLCountry.NETHERLANDS;
    }

    @Override
    public SubdivisionType getDefaultType() {
        return SubdivisionType.PROVINCES;
    }

    @Override
    public SubdivisionType getType() {
        switch (this) {
            case BONAIRE:
            case SABA:
            case SINT_EUSTATIUS:
                return SubdivisionType.SPECIAL_MUNICIPALITIES;
            default:
                return null;
        }
    }

    @Override
    public String getRealName() {
        switch (this) {
            default: return null;
        }
    }

    @Override
    public String getWikipediaURLSuffix(String suffix) {
        switch (this) {
            case GRONINGEN:
            case UTRECHT:
                return "_(province)";
            case LIMBURG:
                return "_(Netherlands)";
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
