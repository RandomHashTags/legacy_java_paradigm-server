package me.randomhashtags.worldlaws.info.agriculture;

import me.randomhashtags.worldlaws.country.SovereignStateInfo;

public enum ProductionFoods implements CountryProductionService {
    APPLE,
    APRICOT,
    AVOCADO,
    CHERRY,
    COCONUT,
    //COFFEE, // https://en.wikipedia.org/wiki/List_of_countries_by_coffee_production
    CUCUMBER,
    EGGPLANT,
    GARLIC,
    GRAPE,
    PAPAYA,
    PEAR,
    PINEAPPLE,
    PLUM,
    POTATO,
    //SALT, // https://en.wikipedia.org/wiki/List_of_countries_by_salt_production
    SOYBEAN,
    //STEEL, //https://en.wikipedia.org/wiki/List_of_countries_by_steel_production
    TOMATO,
    ;

    //

    @Override
    public SovereignStateInfo getInfo() {
        return SovereignStateInfo.valueOf("AGRICULTURE_FOOD_" + name() + "_PRODUCTION");
    }

    @Override
    public String getURL() {
        return "https://en.wikipedia.org/wiki/List_of_countries_by_" + name().toLowerCase() + "_production";
    }

    @Override
    public String getSuffix() {
        return " tonnes";
    }
}
