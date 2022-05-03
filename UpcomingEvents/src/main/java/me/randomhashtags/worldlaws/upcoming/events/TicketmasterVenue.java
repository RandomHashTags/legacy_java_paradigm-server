package me.randomhashtags.worldlaws.upcoming.events;

import me.randomhashtags.worldlaws.country.Location;
import me.randomhashtags.worldlaws.locale.JSONObjectTranslatable;
import org.json.JSONObject;

public final class TicketmasterVenue {
    private final String name, imageURL, countryCode, subdivisionName, cityName, generalRule, childRule, parkingDetail, accessibleSeatingInfo;
    private final Location location;

    public TicketmasterVenue(JSONObject json) {
        name = json.getString("name");
        imageURL = json.optString("imageURL", null);
        countryCode = json.getString("countryCode");
        subdivisionName = json.optString("subdivisionName", null);
        cityName = json.getString("cityName");
        location = json.has("location") ? new Location(json.getJSONArray("location")) : null;
        generalRule = json.optString("generalRule", null);
        childRule = json.optString("childRule", null);
        parkingDetail = json.optString("parkingDetail", null);
        accessibleSeatingInfo = json.optString("accessibleSeatingInfo", null);
    }
    public TicketmasterVenue(String name, String imageURL, Location location, String countryCode, String subdivisionName, String cityName, String generalRule, String childRule, String parkingDetail, String accessibleSeatingInfo) {
        this.name = name;
        this.imageURL = imageURL;
        this.location = location;
        this.countryCode = countryCode;
        this.subdivisionName = subdivisionName;
        this.cityName = cityName;
        this.generalRule = generalRule;
        this.childRule = childRule;
        this.parkingDetail = parkingDetail;
        this.accessibleSeatingInfo = accessibleSeatingInfo;
    }

    public String getName() {
        return name;
    }
    public JSONObjectTranslatable toJSONObject() {
        final JSONObjectTranslatable json = new JSONObjectTranslatable("generalRule", "childRule", "parkingDetail", "accessibleSeatingInfo");
        if(imageURL != null) {
            json.put("imageURL", imageURL);
        }
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
        if(parkingDetail != null) {
            json.put("parkingDetail", parkingDetail);
        }
        if(accessibleSeatingInfo != null) {
            json.put("accessibleSeatingInfo", accessibleSeatingInfo);
        }
        return json;
    }
}