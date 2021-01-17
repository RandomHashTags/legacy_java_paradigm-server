package me.randomhashtags.worldlaws.info.list;

import me.randomhashtags.worldlaws.location.Location;

public final class FlyoverObj {
    private final String country, territory, city;

    public FlyoverObj(String country, String territory, String city) {
        this.country = country;
        this.territory = territory;
        this.city = city;
    }

    public String getTerritory() {
        return territory;
    }
    public String getCity() {
        return city;
    }

    public Location getCoordinates() {
        switch (country.toLowerCase()) {
            case "australia": return getAustraliaLocation();
            case "austria": return null;
            case "belgium": return null;
            case "canada": return getCanadaLocation();
            case "czech republic": return null;
            case "denmark": return getDenmarkLocation();
            case "finland": return getFinlandLocation();
            case "france": return null;
            case "germany": return null;
            case "hungary": return null;
            case "ireland": return null;
            case "italy": return null;
            case "japan": return null;
            case "mexico": return null;
            case "netherlands": return null;
            case "new zealand": return null;
            case "portugal": return null;
            case "south africa": return null;
            case "spain": return getSpainLocation();
            case "sweden": return getSwedenLocation();
            case "switzerland": return getSwitzerlandLocation();
            case "taiwan": return null;
            default: return null;
        }
    }

    @Override
    public String toString() {
        final Location coordinates = getCoordinates();
        return "{" +
                "\"territory\":\"" + territory + "\"," +
                "\"city\":\"" + city + "\"," +
                "\"coordinates\":" + (coordinates != null ? coordinates.toString() : "null") +
                "}";
    }

    private Location getAustraliaLocation() {
        switch (city.toLowerCase()) {
            case "adelaide": return new Location(-34.928889, 138.601111);
            case "canberra": return new Location(-35.293056, 149.126944);
            case "gold coast": return new Location(-28.016667, 153.4);
            case "melbourne": return new Location(-37.813611, 144.963056);
            case "newcastle": return new Location(-32.916667, 151.75);
            case "perth": return new Location(-31.952222, 115.858889);
            case "sydney": return new Location(-33.865, 151.209444);
            case "twelve apostles": return new Location(-38.665833, 143.104444);
            default: return null;
        }
    }
    private Location getCanadaLocation() {
        switch (city.toLowerCase()) {
            case "calgary": return new Location(51.05, -114.066667);
            case "montréal": return new Location(45.508889, -73.561667);
            case "toronto": return new Location(43.741667, -79.373333);
            case "vancouver": return new Location(49.260833, -123.113889);
            default: return null;
        }
    }
    private Location getDenmarkLocation() {
        switch (city.toLowerCase()) {
            case "aarhus": return new Location(56.15, 10.216667);
            case "copenhagen": return new Location(55.676111, 12.568333);
            case "odense": return new Location(55.395833, 10.388611);
            default: return null;
        }
    }
    private Location getFinlandLocation() {
        switch (city.toLowerCase()) {
            case "helsinki": return new Location(60.170833, 24.9375);
            default: return null;
        }
    }
    private Location getSpainLocation() {
        switch (city.toLowerCase()) {
            default: return null;
        }
    }
    private Location getSwedenLocation() {
        switch (city.toLowerCase()) {
            case "gothenburg": return new Location(57.7, 11.966667);
            case "helsingborg": return new Location(56.05, 12.716667);
            case "stockholm": return new Location(59.329444, 18.068611);
            case "visby": return new Location(57.634722, 18.299167);
            default: return null;
        }
    }
    private Location getSwitzerlandLocation() {
        switch (city.toLowerCase()) {
            case "basel": return new Location(47.554722, 7.590556);
            case "berne": return new Location(46.948056, 7.4475);
            default: return null;
        }
    }
}
