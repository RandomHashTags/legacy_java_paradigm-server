package me.randomhashtags.worldlaws.info.agriculture.todo;

import me.randomhashtags.worldlaws.info.agriculture.CountryProductionService;
import me.randomhashtags.worldlaws.country.SovereignStateInfo;

public enum ProductionFoodArtichoke implements CountryProductionService {
    INSTANCE;

    @Override
    public SovereignStateInfo getInfo() {
        return SovereignStateInfo.AGRICULTURE_FOOD_ARTICHOKE_PRODUCTION;
    }

    @Override
    public String getURL() {
        return "https://en.wikipedia.org/wiki/List_of_countries_by_artichoke_production";
    }

    @Override
    public String getSuffix() {
        return " tonnes";
    }
}
