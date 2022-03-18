package me.randomhashtags.worldlaws.earthquakes;

import me.randomhashtags.worldlaws.LocalServer;
import me.randomhashtags.worldlaws.country.Location;
import me.randomhashtags.worldlaws.locale.JSONObjectTranslatable;

public final class PreEarthquake {
    private final String id, place, country, subdivision;
    private final Location location;

    public PreEarthquake(String id, String place, String country, String subdivision, Location location) {
        this.id = id;
        this.place = LocalServer.fixEscapeValues(place);
        this.country = country;
        this.subdivision = subdivision;
        this.location = location;
    }

    public String getID() {
        return id;
    }

    public JSONObjectTranslatable toJSONObject() {
        final JSONObjectTranslatable json = new JSONObjectTranslatable("place");
        json.put("place", place);
        if(country != null) {
            json.put("country", country);
        }
        if(subdivision != null) {
            json.put("subdivision", subdivision);
        }
        json.put("location", location.toJSONArray());
        return json;
    }
}
