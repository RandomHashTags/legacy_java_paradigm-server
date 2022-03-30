package me.randomhashtags.worldlaws.country.subdivisions.r;

import me.randomhashtags.worldlaws.country.SovereignStateSubdivision;
import me.randomhashtags.worldlaws.country.WLCountry;
import me.randomhashtags.worldlaws.country.subdivisions.SubdivisionType;

public enum SubdivisionsRwanda implements SovereignStateSubdivision { //https://en.wikipedia.org/wiki/Provinces_of_Rwanda
    EASTERN,
    KIGALI,
    NORTHERN,
    SOUTHERN,
    WESTERN,
    ;

    @Override
    public WLCountry getCountry() {
        return WLCountry.RWANDA;
    }

    @Override
    public SubdivisionType getDefaultType() {
        return SubdivisionType.PROVINCES;
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
            case KIGALI:
                return null;
            default:
                return "_Province,_Rwanda";
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
