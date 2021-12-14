package me.randomhashtags.worldlaws.country.subdivisions.a;

import me.randomhashtags.worldlaws.country.SovereignStateSubdivision;
import me.randomhashtags.worldlaws.country.WLCountry;

public enum SubdivisionsAustralia implements SovereignStateSubdivision { // https://en.wikipedia.org/wiki/States_and_territories_of_Australia
    NEW_SOUTH_WALES,
    QUEENSLAND,
    SOUTH_AUSTRALIA,
    TASMANIA,
    VICTORIA,
    WESTERN_AUSTRALIA,
    ;

    @Override
    public WLCountry getCountry() {
        return WLCountry.AUSTRALIA;
    }

    @Override
    public String getISOAlpha2() {
        switch (this) {
            case NEW_SOUTH_WALES: return "NSW";
            case QUEENSLAND: return "QLD";
            case SOUTH_AUSTRALIA: return "SA";
            case TASMANIA: return "TAS";
            case VICTORIA: return "VIC";
            case WESTERN_AUSTRALIA: return "WA";
            default: return null;
        }
    }

    @Override
    public String getFlagURL() {
        return null;
    }

    @Override
    public String getGovernmentWebsite() {
        return "https://www." + getISOAlpha2().toLowerCase() + ".gov.au";
    }
}
