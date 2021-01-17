package me.randomhashtags.worldlaws.info.agriculture;

import me.randomhashtags.worldlaws.location.CountryInfo;

import java.util.HashMap;

public enum ProductionFoodGrape implements CountryProductionService {
    INSTANCE;

    private HashMap<String, String> countries;

    @Override
    public CountryInfo getInfo() {
        return CountryInfo.AGRICULTURE_FOOD_GRAPE_PRODUCTION;
    }

    @Override
    public String getURL() {
        return "https://en.wikipedia.org/wiki/List_of_countries_by_grape_production";
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
