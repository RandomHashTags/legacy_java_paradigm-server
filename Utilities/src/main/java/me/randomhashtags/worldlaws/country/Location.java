package me.randomhashtags.worldlaws.country;

import org.json.JSONObject;

public final class Location {
    private final double longitude, latitude;

    public Location(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    @Override
    public String toString() {
        return toJSONObject().toString();
    }

    public JSONObject toJSONObject() {
        final JSONObject json = new JSONObject();
        json.put("latitude", latitude);
        json.put("longitude", longitude);
        return json;
    }
}
