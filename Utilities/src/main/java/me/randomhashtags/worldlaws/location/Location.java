package me.randomhashtags.worldlaws.location;

public final class Location {
    private final float longitude, latitude;

    public Location(float latitude, float longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public float getLatitude() {
        return latitude;
    }
    public float getLongitude() {
        return longitude;
    }

    @Override
    public String toString() {
        return "{" +
                "\"latitude\":" + latitude + "," +
                "\"longitude\":" + longitude +
                "}";
    }
}
