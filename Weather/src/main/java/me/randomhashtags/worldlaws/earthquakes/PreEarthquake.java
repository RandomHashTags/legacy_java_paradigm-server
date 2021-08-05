package me.randomhashtags.worldlaws.earthquakes;

import me.randomhashtags.worldlaws.LocalServer;
import me.randomhashtags.worldlaws.country.Location;

public final class PreEarthquake {
    private final String id, place, magnitude;
    private final Location location;

    public PreEarthquake(String id, String place, String magnitude, Location location) {
        this.id = id;
        this.place = LocalServer.fixEscapeValues(place);
        this.magnitude = magnitude;
        this.location = location;
    }

    public String getMagnitude() {
        return magnitude;
    }

    @Override
    public String toString() {
        return "\"" + id + "\":{" +
                "\"place\":\"" + place + "\"," +
                "\"location\":" + location.toString() +
                "}";
    }
}
