package me.randomhashtags.worldlaws.earthquakes;

import me.randomhashtags.worldlaws.LocalServer;
import me.randomhashtags.worldlaws.country.Location;

public final class PreEarthquake {
    private final String id, place, country, subdivision, magnitude;
    private final Location location;

    public PreEarthquake(String id, String place, String country, String subdivision, String magnitude, Location location) {
        this.id = id;
        this.place = LocalServer.fixEscapeValues(place);
        this.country = country;
        this.subdivision = subdivision;
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
                (country != null ? "\"country\":\"" + country + "\"," : "") +
                (subdivision != null ? "\"subdivision\":\"" + subdivision + "\"," : "") +
                "\"location\":" + location.toString() +
                "}";
    }
}
