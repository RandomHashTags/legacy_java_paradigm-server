package me.randomhashtags.worldlaws.earthquakes;

import me.randomhashtags.worldlaws.EventSources;
import me.randomhashtags.worldlaws.country.Location;
import me.randomhashtags.worldlaws.locale.JSONObjectTranslatable;

public final class Earthquake {
    private final String country, subdivision, cause, place;
    private final float magnitude, depthKM;
    private final long exactTimeMilliseconds, lastUpdated;
    private final Location location;
    private final EventSources sources;

    public Earthquake(String country, String subdivision, String cause, float magnitude, String place, long exactTimeMilliseconds, long lastUpdated, float depthKM, Location location, EventSources sources) {
        this.country = country;
        this.subdivision = subdivision;
        this.cause = cause;
        this.magnitude = magnitude;
        this.place = place;
        this.exactTimeMilliseconds = exactTimeMilliseconds;
        this.lastUpdated = lastUpdated;
        this.depthKM = depthKM;
        this.location = location;
        this.sources = sources;
    }

    public JSONObjectTranslatable toJSONObject() {
        final JSONObjectTranslatable json = new JSONObjectTranslatable("place");
        json.put("cause", cause);
        json.put("magnitude", magnitude);
        json.put("place", place);
        if(country != null) {
            json.put("country", country);
        }
        if(subdivision != null) {
            json.put("subdivision", subdivision);
        }
        json.put("exactTimeMilliseconds", exactTimeMilliseconds);
        json.put("lastUpdated", lastUpdated);
        if(depthKM > 0.00) {
            json.put("depthKM", depthKM);
        }
        json.put("location", location);
        json.put("sources", sources.toJSONObject());
        return json;
    }
}
