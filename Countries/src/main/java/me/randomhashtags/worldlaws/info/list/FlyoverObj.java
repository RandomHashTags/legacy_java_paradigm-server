package me.randomhashtags.worldlaws.info.list;

import me.randomhashtags.worldlaws.country.Location;

public final class FlyoverObj {
    private final String city;
    private final Location location;

    public FlyoverObj(String city, Location location) {
        this.city = city;
        this.location = location;
    }

    @Override
    public String toString() {
        return "\"" + city + "\":" + location.toString();
    }
}
