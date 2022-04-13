package me.randomhashtags.worldlaws.country.cities;

import me.randomhashtags.worldlaws.locale.JSONObjectTranslatable;

public final class City {
    private final CityController controller;
    private final String name;

    public City(CityController controller, String name) {
        this.controller = controller;
        this.name = name;
    }


    public CityController getController() {
        return controller;
    }

    public JSONObjectTranslatable toJSONObject() {
        final JSONObjectTranslatable json = new JSONObjectTranslatable("name");
        json.put("name", name);
        return json;
    }
}
