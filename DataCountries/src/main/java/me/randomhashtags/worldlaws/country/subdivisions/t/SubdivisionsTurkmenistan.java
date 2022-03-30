package me.randomhashtags.worldlaws.country.subdivisions.t;

import me.randomhashtags.worldlaws.country.SovereignStateSubdivision;
import me.randomhashtags.worldlaws.country.WLCountry;
import me.randomhashtags.worldlaws.country.subdivisions.SubdivisionType;

public enum SubdivisionsTurkmenistan implements SovereignStateSubdivision { // https://en.wikipedia.org/wiki/Regions_of_Turkmenistan
    AHAL,
    ASHGABAT,
    BALKAN,
    DASOGUZ,
    LEBAP,
    MARY,
    ;

    @Override
    public WLCountry getCountry() {
        return WLCountry.TURKMENISTAN;
    }

    @Override
    public SubdivisionType getDefaultType() {
        return SubdivisionType.PROVINCES;
    }

    @Override
    public String getRealName() {
        switch (this) {
            case DASOGUZ: return "Daşoguz";
            default: return null;
        }
    }

    @Override
    public String getWikipediaURLSuffix(String suffix) {
        switch (this) {
            case ASHGABAT: return null;
            default: return "_Region";
        }
    }

    @Override
    public String getISOAlpha2() {
        switch (this) {
            case AHAL: return "A";
            case ASHGABAT: return "S";
            case BALKAN: return "B";
            case DASOGUZ: return "D";
            case LEBAP: return "L";
            case MARY: return "M";
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
