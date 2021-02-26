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

    private final CountryInfo info;
    private final String url;
    private HashMap<String, String> countries;

    ProductionFoods() {
        final String name = name();
        info = CountryInfo.valueOf("AGRICULTURE_FOOD_" + name + "_PRODUCTION");
        url = "https://en.wikipedia.org/wiki/List_of_countries_by_" + name.toLowerCase() + "_production";
    }

    @Override
    public String getURL() {
        return url;
    }

    @Override
    public void setCountries(HashMap<String, String> countries) {
        this.countries = countries;
    }

    @Override
    public CountryInfo getInfo() {
        return info;
    }

    @Override
    public HashMap<String, String> getCountries() {
        return countries;
    }
}
