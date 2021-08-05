package me.randomhashtags.worldlaws.country;

public final class Location {
    private final double longitude, latitude;

    public Location(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    @Override
    public String toString() {
        return "{" +
                "\"latitude\":" + latitude + "," +
                "\"longitude\":" + longitude +
                "}";
    }
}
