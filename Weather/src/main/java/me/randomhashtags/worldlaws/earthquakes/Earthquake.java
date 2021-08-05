package me.randomhashtags.worldlaws.earthquakes;

import me.randomhashtags.worldlaws.LocalServer;
import me.randomhashtags.worldlaws.country.Location;

public final class Earthquake {
    private final String territory, cause, magnitude, place, url;
    private final float depthKM;
    private final long time, lastUpdated;
    private final Location location;

    public Earthquake(String territory, String cause, String magnitude, String place, long time, long lastUpdated, float depthKM, Location location, String url) {
        this.territory = territory;
        this.cause = cause;
        this.magnitude = magnitude;
        this.place = LocalServer.fixEscapeValues(place);
        this.time = time;
        this.lastUpdated = lastUpdated;
        this.depthKM = depthKM;
        this.location = location;
        this.url = url;
    }

    @Override
    public String toString() {
        return "{" +
                "\"cause\":\"" + cause + "\"," +
                "\"magnitude\":" + magnitude + "," +
                "\"place\":\"" + place + "\"," +
                "\"territory\":\"" + territory + "\"," +
                "\"time\":" + time + "," +
                "\"lastUpdated\":" + lastUpdated + "," +
                "\"depthKM\":" + depthKM + "," +
                "\"location\":" + location.toString() + "," +
                "\"url\":\"" + url + "\"" +
                "}";
    }
}
