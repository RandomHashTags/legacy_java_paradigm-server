package me.randomhashtags.worldlaws.weather.recode;

import me.randomhashtags.worldlaws.location.Location;

import java.util.List;

public final class WeatherZone {
    private final String id, name, territory;
    private final List<Location> geometry;

    public WeatherZone(String id, String name, String territory, List<Location> geometry) {
        this.id = id;
        this.name = name;
        this.territory = territory;
        this.geometry = geometry;
    }

    private String getGeometryJSON() {
        final StringBuilder builder = new StringBuilder("[");
        boolean isFirst = true;
        for(Location point : geometry) {
            builder.append(isFirst ? "" : ",").append(point.toString());
            isFirst = false;
        }
        return builder.append("]").toString();
    }

    @Override
    public String toString() {
        return "{" +
                "\"id\":\"" + id + "\"," +
                "\"name\":\"" + name + "\"," +
                "\"territory\":\"" + territory + "\"," +
                "\"geometry\":" + getGeometryJSON() +
                "}";
    }
}
