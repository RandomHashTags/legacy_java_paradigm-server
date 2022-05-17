package me.randomhashtags.worldlaws.earthquakes;

import me.randomhashtags.worldlaws.country.Location;
import me.randomhashtags.worldlaws.locale.JSONObjectTranslatable;

public final class PreEarthquake {
    private final long exactTimeMilliseconds;
    private final String id, place, country, subdivision;
    private final Location location;

    public PreEarthquake(long exactTimeMilliseconds, String id, String place, String country, String subdivision, Location location) {
        this.exactTimeMilliseconds = exactTimeMilliseconds;
        this.id = id;
        this.place = place;
        this.country = country;
        this.subdivision = subdivision;
        this.location = location;
    }

    public String getID() {
        return id;
    }

    public JSONObjectTranslatable toJSONObject() {
        final JSONObjectTranslatable json = new JSONObjectTranslatable("place");
        json.put("exactTimeMilliseconds", exactTimeMilliseconds);
        json.put("place", place);
        if(country != null) {
            json.put("country", country);
        }
        if(subdivision != null) {
            json.put("subdivision", subdivision);
        }
        json.put("location", location);
        return json;
    }
}
