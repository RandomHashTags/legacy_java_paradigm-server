package me.randomhashtags.worldlaws.upcoming.events;

import me.randomhashtags.worldlaws.LocalServer;
import me.randomhashtags.worldlaws.country.Location;

public final class TicketmasterVenue {
    private final String name, imageURL, countryCode, stateCode, cityName, generalRule, childRule, parkingDetail, accessibleSeatingInfo;
    private final Location location;

    public TicketmasterVenue(String name, String imageURL, Location location, String countryCode, String stateCode, String cityName, String generalRule, String childRule, String parkingDetail, String accessibleSeatingInfo) {
        this.name = LocalServer.fixEscapeValues(name);
        this.imageURL = imageURL;
        this.location = location;
        this.countryCode = countryCode;
        this.stateCode = stateCode;
        this.cityName = LocalServer.fixEscapeValues(cityName);
        this.generalRule = LocalServer.fixEscapeValues(generalRule);
        this.childRule = LocalServer.fixEscapeValues(childRule);
        this.parkingDetail = LocalServer.fixEscapeValues(parkingDetail);
        this.accessibleSeatingInfo = LocalServer.fixEscapeValues(accessibleSeatingInfo);
    }

    @Override
    public String toString() {
        return "\"" + name + "\":{" +
                "\"imageURL\":\"" + imageURL + "\"," +
                "\"countryCode\":\"" + countryCode + "\"," +
                "\"subdivisionCode\":\"" + stateCode + "\"," +
                "\"cityName\":\"" + cityName + "\"," +
                "\"location\":" + location.toString() + "," +
                "\"generalRule\":\"" + generalRule + "\"," +
                "\"childRule\":\"" + childRule + "\"," +
                "\"parkingDetail\":\"" + parkingDetail + "\"," +
                "\"accessibilitySeatingInfo\":\"" + accessibleSeatingInfo + "\"" +
                "}";
    }
}