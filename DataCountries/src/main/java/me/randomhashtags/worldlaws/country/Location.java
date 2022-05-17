package me.randomhashtags.worldlaws.country;

import org.json.JSONArray;

public final class Location extends JSONArray {
    public Location(JSONArray array) {
        this(array.getDouble(0), array.getDouble(1));
    }
    public Location(double latitude, double longitude) {
        put(latitude);
        put(longitude);
    }
}
