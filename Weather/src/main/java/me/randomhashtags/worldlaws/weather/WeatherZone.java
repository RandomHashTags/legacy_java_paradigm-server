package me.randomhashtags.worldlaws.weather;

import me.randomhashtags.worldlaws.LocalServer;
import me.randomhashtags.worldlaws.country.Location;

import java.util.List;

public final class WeatherZone {
    private final String name, nameSuffix, subdivision;
    private final List<Location> geometry;

    public WeatherZone(String name, String nameSuffix, String subdivision, List<Location> geometry) {
        this.name = LocalServer.fixEscapeValues(name);
        this.nameSuffix = LocalServer.fixEscapeValues(nameSuffix);
        this.subdivision = LocalServer.fixEscapeValues(subdivision);
        this.geometry = geometry;
    }

    public String getName() {
        return name;
    }
    public String getSubdivision() {
        return subdivision;
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
                (nameSuffix != null ? "\"nameSuffix\":\"" + nameSuffix + "\"," : "") +
                "\"subdivision\":\"" + subdivision + "\"," +
                "\"geometry\":" + getGeometryJSON() +
                "}";
    }
}
