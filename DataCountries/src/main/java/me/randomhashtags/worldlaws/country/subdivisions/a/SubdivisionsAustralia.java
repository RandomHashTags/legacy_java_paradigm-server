package me.randomhashtags.worldlaws.country.subdivisions.a;

import me.randomhashtags.worldlaws.country.SovereignStateSubdivision;
import me.randomhashtags.worldlaws.country.WLCountry;
import me.randomhashtags.worldlaws.country.subdivisions.SubdivisionType;

public enum SubdivisionsAustralia implements SovereignStateSubdivision { // https://en.wikipedia.org/wiki/States_and_territories_of_Australia
    NEW_SOUTH_WALES,
    QUEENSLAND,
    SOUTH_AUSTRALIA,
    TASMANIA,
    VICTORIA,
    WESTERN_AUSTRALIA,

    AUSTRALIAN_CAPITAL_TERRITORY,
    JERVIS_BAY_TERRITORY,
    NORTHERN_TERRITORY,

    ASHMORE_AND_CARTIER_ISLANDS,
    AUSTRALIAN_ANTARCTIC_TERRITORY,
    CHRISTMAS_ISLAND,
    COCOS_KEELING_ISLANDS,
    CORAL_SEA_ISLANDS,
    HEARD_ISLAND_AND_MCDONALD_ISLANDS,
    NORFOLK_ISLAND,
    ;

    @Override
    public WLCountry getCountry() {
        return WLCountry.AUSTRALIA;
    }

    @Override
    public SubdivisionType getDefaultType() {
        return SubdivisionType.STATES;
    }

    @Override
    public SubdivisionType getType() {
        switch (this) {
            case AUSTRALIAN_CAPITAL_TERRITORY:
            case JERVIS_BAY_TERRITORY:
            case NORTHERN_TERRITORY:

            case ASHMORE_AND_CARTIER_ISLANDS:
            case AUSTRALIAN_ANTARCTIC_TERRITORY:
            case CHRISTMAS_ISLAND:
            case COCOS_KEELING_ISLANDS:
            case CORAL_SEA_ISLANDS:
            case HEARD_ISLAND_AND_MCDONALD_ISLANDS:
            case NORFOLK_ISLAND:
                return SubdivisionType.FEDERAL_TERRITORIES;
            default: return null;
        }
    }

    @Override
    public String getRealName() {
        switch (this) {
            case COCOS_KEELING_ISLANDS: return "Cocos (Keeling) Islands";
            case HEARD_ISLAND_AND_MCDONALD_ISLANDS: return "Heard Island and McDonald Islands";
            default: return null;
        }
    }

    @Override
    public String getWikipediaURL() {
        switch (this) {
            case VICTORIA:
                return "https://en.wikipedia.org/wiki/Victoria_(Australia)";
            default:
                return SovereignStateSubdivision.super.getWikipediaURL();
        }
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
