package me.randomhashtags.worldlaws.location;

public final class Location {
    private final double longitude, latitude;

    public Location(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }
    public double getLongitude() {
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
