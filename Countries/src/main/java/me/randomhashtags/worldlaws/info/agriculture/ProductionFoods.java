package me.randomhashtags.worldlaws.info.agriculture;

import me.randomhashtags.worldlaws.location.SovereignStateInfo;

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
