package me.randomhashtags.worldlaws.country.subdivisions.b;

import me.randomhashtags.worldlaws.country.SovereignStateSubdivision;
import me.randomhashtags.worldlaws.country.WLCountry;
import me.randomhashtags.worldlaws.country.subdivisions.SubdivisionType;

public enum SubdivisionsBelarus implements SovereignStateSubdivision { // https://en.wikipedia.org/wiki/Regions_of_Belarus
    BREST,
    GOMEL,
    GRODNO,
    MINSK,
    MINSK_CITY,
    MOGILEV,
    VITESBSK,
    ;

    @Override
    public WLCountry getCountry() {
        return WLCountry.BELARUS;
    }

    @Override
    public SubdivisionType getDefaultType() {
        return SubdivisionType.OBLASTS;
    }

    @Override
    public SubdivisionType getType() {
        switch (this) {
            case MINSK_CITY: return SubdivisionType.SPECIAL_CITIES;
            default: return null;
        }
    }

    @Override
    public String getConditionalName() {
        switch (this) {
            case MINSK_CITY:
                return "Minsk";
            default:
                return SovereignStateSubdivision.super.getConditionalName();
        }
    }

    @Override
    public String getWikipediaURLSuffix(String suffix) {
        switch (this) {
            case MINSK_CITY:
                return "";
            default:
                return "_" + suffix;
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
