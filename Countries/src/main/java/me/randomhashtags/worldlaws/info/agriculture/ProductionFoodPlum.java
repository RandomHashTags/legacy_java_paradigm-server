package me.randomhashtags.worldlaws.info.agriculture;

import me.randomhashtags.worldlaws.location.CountryInfo;

import java.util.HashMap;

public enum ProductionFoodPlum implements CountryProductionService {
    INSTANCE;

    private HashMap<String, String> countries;

    @Override
    public CountryInfo getInfo() {
        return CountryInfo.AGRICULTURE_FOOD_PLUM_PRODUCTION;
    }

    @Override
    public String getURL() {
        return "https://en.wikipedia.org/wiki/List_of_countries_by_plum_production";
    }

    @Override
    public String getSuffix() {
        return " tonnes";
    }

    @Override
    public HashMap<String, String> getCountries() {
        return countries;
    }

    @Override
    public void setCountries(HashMap<String, String> countries) {
        this.countries = countries;
    }
}
