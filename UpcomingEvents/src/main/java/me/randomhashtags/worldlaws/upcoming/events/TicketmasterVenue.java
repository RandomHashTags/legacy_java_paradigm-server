package me.randomhashtags.worldlaws.upcoming.events;

import me.randomhashtags.worldlaws.LocalServer;
import me.randomhashtags.worldlaws.country.Location;
import org.json.JSONObject;

public final class TicketmasterVenue {
    private final String name, imageURL, countryCode, subdivisionName, cityName, generalRule, childRule, parkingDetail, accessibleSeatingInfo;
    private final Location location;

    public TicketmasterVenue(JSONObject json) {
        name = json.getString("name");
        imageURL = json.getString("imageURL");
        countryCode = json.getString("countryCode");
        subdivisionName = json.optString("subdivisionName", null);
        cityName = json.getString("cityName");
        location = json.has("location") ? new Location(json.getJSONArray("location")) : null;
        generalRule = json.optString("generalRule", null);
        childRule = json.optString("childRule", null);
        parkingDetail = json.getString("parkingDetail");
        accessibleSeatingInfo = json.getString("accessibleSeatingInfo");
    }
    public TicketmasterVenue(String name, String imageURL, Location location, String countryCode, String subdivisionName, String cityName, String generalRule, String childRule, String parkingDetail, String accessibleSeatingInfo) {
        this.name = LocalServer.fixEscapeValues(name);
        this.imageURL = imageURL;
        this.location = location;
        this.countryCode = countryCode;
        this.subdivisionName = subdivisionName;
        this.cityName = LocalServer.fixEscapeValues(cityName);
        this.generalRule = LocalServer.fixEscapeValues(generalRule);
        this.childRule = LocalServer.fixEscapeValues(childRule);
        this.parkingDetail = LocalServer.fixEscapeValues(parkingDetail);
        this.accessibleSeatingInfo = LocalServer.fixEscapeValues(accessibleSeatingInfo);
    }

    public String getName() {
        return name;
    }
    public JSONObject toJSONObject() {
        final JSONObject json = new JSONObject();
        json.put("imageURL", imageURL);
        json.put("countryCode", countryCode);
        if(subdivisionName != null) {
            json.put("subdivisionName", subdivisionName);
        }
        json.put("cityName", cityName);
        if(location != null) {
            json.put("location", location.toJSONArray());
        }
        if(generalRule != null) {
            json.put("generalRule", generalRule);
        }
        if(childRule != null) {
            json.put("childRule", childRule);
        }
        json.put("parkingDetail", parkingDetail);
        json.put("accessibleSeatingInfo", accessibleSeatingInfo);
        return json;
    }
}