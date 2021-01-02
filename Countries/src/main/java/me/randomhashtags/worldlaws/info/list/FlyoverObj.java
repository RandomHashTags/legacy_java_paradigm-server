package me.randomhashtags.worldlaws.info.list;

import java.util.List;

public final class FlyoverObj {
    private final String territory;
    private final List<String> cities;

    public FlyoverObj(String territory, List<String> cities) {
        this.territory = territory;
        this.cities = cities;
    }

    public String getTerritory() {
        return territory;
    }

    private String citiesString() {
        final StringBuilder builder = new StringBuilder("[");
        boolean isFirst = true;
        for(String city : cities) {
            builder.append(isFirst ? "" : ",").append("\"").append(city).append("\"");
            isFirst = false;
        }
        builder.append("]");
        return builder.toString();
    }
    public void addCity(String city) {
        cities.add(city);
    }

    @Override
    public String toString() {
        return "{" +
                "\"territory\":\"" + territory + "\"," +
                "\"cities\":" + citiesString() + "" +
                "}";
    }
}
