package me.randomhashtags.worldlaws.country.subdivisions;

import me.randomhashtags.worldlaws.CompletionHandler;
import me.randomhashtags.worldlaws.country.SovereignStateSubdivision;
import me.randomhashtags.worldlaws.country.WLCountry;

public enum AustraliaSubdivisions implements SovereignStateSubdivision {
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
    public String getPostalCodeAbbreviation() {
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
    public String getGovernmentURL() {
        return "https://www." + getPostalCodeAbbreviation().toLowerCase() + ".gov.au";
    }

    @Override
    public void getCitiesHashSet(CompletionHandler handler) {
    }
}
