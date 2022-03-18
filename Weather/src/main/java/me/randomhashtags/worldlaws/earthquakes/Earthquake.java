package me.randomhashtags.worldlaws.earthquakes;

import me.randomhashtags.worldlaws.EventSources;
import me.randomhashtags.worldlaws.LocalServer;
import me.randomhashtags.worldlaws.country.Location;
import me.randomhashtags.worldlaws.locale.JSONObjectTranslatable;

public final class Earthquake {
    private final String country, subdivision, cause, magnitude, place;
    private final float depthKM;
    private final long time, lastUpdated;
    private final Location location;
    private final EventSources sources;

    public Earthquake(String country, String subdivision, String cause, String magnitude, String place, long time, long lastUpdated, float depthKM, Location location, EventSources sources) {
        this.country = country;
        this.subdivision = subdivision;
        this.cause = cause;
        this.magnitude = magnitude;
        this.place = LocalServer.fixEscapeValues(place);
        this.time = time;
        this.lastUpdated = lastUpdated;
        this.depthKM = depthKM;
        this.location = location;
        this.sources = sources;
    }

    @Override
    public String toString() {
        return "{" +
                "\"cause\":\"" + cause + "\"," +
                "\"magnitude\":" + magnitude + "," +
                "\"place\":\"" + place + "\"," +
                (country != null ? "\"country\":\"" + country + "\"," : "") +
                (subdivision != null ? "\"subdivision\":\"" + subdivision + "\"," : "") +
                "\"time\":" + time + "," +
                "\"lastUpdated\":" + lastUpdated + "," +
                (depthKM > 0.00 ? "\"depthKM\":" + depthKM + "," : "") +
                "\"location\":" + location.toString() + "," +
                "\"sources\":" + sources.toString() +
                "}";
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
        json.put("time", time);
        json.put("lastUpdated", lastUpdated);
        if(depthKM > 0.00) {
            json.put("depthKM", depthKM);
        }
        json.put("location", location.toJSONArray());
        json.put("sources", sources.toJSONObject());
        return json;
    }
}
