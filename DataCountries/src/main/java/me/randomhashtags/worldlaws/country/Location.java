package me.randomhashtags.worldlaws.country;

import org.json.JSONArray;

public final class Location {
    private final double longitude, latitude;

    public Location(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    @Override
    public String toString() {
        return toJSONArray().toString();
    }

    public JSONArray toJSONArray() {
        final JSONArray array = new JSONArray();
        array.put(latitude);
        array.put(longitude);
        return array;
    }
}
