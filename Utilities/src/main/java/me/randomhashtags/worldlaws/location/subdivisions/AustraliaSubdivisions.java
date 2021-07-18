package me.randomhashtags.worldlaws.location.subdivisions;

import me.randomhashtags.worldlaws.location.SovereignStateSubdivision;

public enum AustraliaSubdivisions implements SovereignStateSubdivision {
    NEW_SOUTH_WALES,
    QUEENSLAND,
    SOUTH_AUSTRALIA,
    TASMANIA,
    VICTORIA,
    WESTERN_AUSTRALIA,
    ;

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
}
