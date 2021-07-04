package me.randomhashtags.worldlaws.location;

import me.randomhashtags.worldlaws.CompletionHandler;
import me.randomhashtags.worldlaws.LocalServer;

public enum WLCities { // TODO: finish
    INSTANCE;

    public void getFromTerritory(WLCountry country, String territory, CompletionHandler handler) {
        switch (country) {
            case UNITED_STATES:
                getUnitedStatesCities(territory, handler);
                break;
            default:
                break;
        }
    }

    private void getUnitedStatesCities(String territory, CompletionHandler handler) {
        // https://en.wikipedia.org/wiki/Category:Lists_of_cities_in_the_United_States_by_state
        territory = LocalServer.toCorrectCapitalization(territory);
        final String url = "https://en.wikipedia.org/wiki/List_of_cities_in_" + territory;
    }
}
