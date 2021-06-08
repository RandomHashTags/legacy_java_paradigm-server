package me.randomhashtags.worldlaws.info.list;

import me.randomhashtags.worldlaws.location.Location;

public final class FlyoverObj {
    private final String countryBackendID, territory, city;
    private final Location location;

    public FlyoverObj(String countryBackendID, String territory, String city, Location location) {
        this.countryBackendID = countryBackendID;
        this.territory = territory;
        this.city = city;
        this.location = location;
    }

    public String getCountryBackendID() {
        return countryBackendID;
    }
    public String getTerritory() {
        return territory;
    }
    public String getCity() {
        return city;
    }
    public Location getLocation() {
        return location;
    }

    @Override
    public String toString() {
        return "\"" + city + "\":" + location.toString();
    }
}
