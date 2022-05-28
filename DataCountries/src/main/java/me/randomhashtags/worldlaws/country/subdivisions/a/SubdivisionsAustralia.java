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
        switch (this) {
            case NEW_SOUTH_WALES: return "https://upload.wikimedia.org/wikipedia/commons/0/00/Flag_of_New_South_Wales.svg";
            case QUEENSLAND: return "https://upload.wikimedia.org/wikipedia/commons/0/04/Flag_of_Queensland.svg";
            case SOUTH_AUSTRALIA: return "https://upload.wikimedia.org/wikipedia/commons/f/fd/Flag_of_South_Australia.svg";
            case TASMANIA: return "https://upload.wikimedia.org/wikipedia/commons/4/46/Flag_of_Tasmania.svg";
            case VICTORIA: return "https://upload.wikimedia.org/wikipedia/commons/0/08/Flag_of_Victoria_%28Australia%29.svg";
            case WESTERN_AUSTRALIA: return "https://upload.wikimedia.org/wikipedia/commons/a/a5/Flag_of_Western_Australia.svg";

            case AUSTRALIAN_CAPITAL_TERRITORY: return "https://upload.wikimedia.org/wikipedia/commons/8/8c/Flag_of_the_Australian_Capital_Territory.svg";
            case JERVIS_BAY_TERRITORY: return null;
            case NORTHERN_TERRITORY: return "https://upload.wikimedia.org/wikipedia/commons/b/b7/Flag_of_the_Northern_Territory.svg";

            case ASHMORE_AND_CARTIER_ISLANDS: return null;
            case AUSTRALIAN_ANTARCTIC_TERRITORY: return null;
            case CHRISTMAS_ISLAND: return "https://upload.wikimedia.org/wikipedia/commons/6/67/Flag_of_Christmas_Island.svg";
            case COCOS_KEELING_ISLANDS: return "https://upload.wikimedia.org/wikipedia/commons/7/74/Flag_of_the_Cocos_%28Keeling%29_Islands.svg";
            case CORAL_SEA_ISLANDS: return null;
            case HEARD_ISLAND_AND_MCDONALD_ISLANDS: return null;
            case NORFOLK_ISLAND: return "https://upload.wikimedia.org/wikipedia/commons/4/48/Flag_of_Norfolk_Island.svg";
            default: return null;
        }
    }
}
