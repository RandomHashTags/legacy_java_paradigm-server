package me.randomhashtags.worldlaws.info.agriculture;

import me.randomhashtags.worldlaws.location.CountryInfo;

import java.util.HashMap;

public enum ProductionFoods implements CountryProductionService {
    APPLE,
    APRICOT,
    AVOCADO,
    CHERRY,
    COCONUT,
    CUCUMBER,
    EGGPLANT,
    GARLIC,
    GRAPE,
    PAPAYA,
    PEAR,
    PINEAPPLE,
    PLUM,
    POTATO,
    SOYBEAN,
    TOMATO,
    ;

    private HashMap<String, String> countries;

    @Override
    public CountryInfo getInfo() {
        return CountryInfo.valueOf("AGRICULTURE_FOOD_" + name() + "_PRODUCTION");
    }

    @Override
    public String getURL() {
        return "https://en.wikipedia.org/wiki/List_of_countries_by_" + name().toLowerCase() + "_production";
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
