package me.randomhashtags.worldlaws.location.territories;

import me.randomhashtags.worldlaws.LocalServer;
import me.randomhashtags.worldlaws.PopulationEstimate;
import me.randomhashtags.worldlaws.location.Country;
import me.randomhashtags.worldlaws.location.Territory;

// https://en.wikipedia.org/wiki/States_and_territories_of_Australia
public enum ASTerritory implements Territory {
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
    COCOS_ISLAND,
    CORAL_SEA_ISLAND,
    HEAD_ISLAND_AND_MCDONALD_ISLANDS,
    NORFOLK_ISLAND

    ;

    public static final ASTerritory[] TERRITORIES = ASTerritory.values();
    private String name;

    ASTerritory() {
        name = LocalServer.toCorrectCapitalization(name(), "and");
    }

    @Override
    public Country getCountry() {
        return Country.AUSTRALIA;
    }
    @Override
    public String getBackendID() {
        return null;
    }
    @Override
    public String getName() {
        return name;
    }
    @Override
    public String getAbbreviation() {
        return null;
    }
    @Override
    public String getFlagURL() {
        return null;
    }
    @Override
    public PopulationEstimate getPopulationEstimate() {
        return null;
    }
    @Override
    public String getGovernmentURL() {
        return null;
    }
}
