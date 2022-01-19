package me.randomhashtags.worldlaws.weather;

import me.randomhashtags.worldlaws.country.Location;

import java.util.List;

public final class WeatherZone {
    private final String name, territory;
    private final List<Location> geometry;

    public WeatherZone(String name, String territory, List<Location> geometry) {
        this.name = name;
        this.territory = territory;
        this.geometry = geometry;
    }

    public String getName() {
        return name;
    }
    public String getSubdivision() {
        return territory;
    }

    private String getGeometryJSON() {
        final StringBuilder builder = new StringBuilder("[");
        boolean isFirst = true;
        for(Location point : geometry) {
            if(point != null) {
                builder.append(isFirst ? "" : ",").append(point.toString());
                isFirst = false;
            }
        }
        return builder.append("]").toString();
    }

    @Override
    public String toString() {
        return "{" +
                "\"name\":\"" + name + "\"," +
                "\"territory\":\"" + territory + "\"," +
                "\"geometry\":" + getGeometryJSON() +
                "}";
    }
}
