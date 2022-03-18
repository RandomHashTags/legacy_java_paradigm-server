package me.randomhashtags.worldlaws.weather;

import me.randomhashtags.worldlaws.EventSource;
import me.randomhashtags.worldlaws.locale.JSONObjectTranslatable;
import org.json.JSONArray;

import java.util.HashSet;

public final class WeatherAlert {
    private final String event, certainty, headline, instruction, description;
    private final HashSet<WeatherZone> zones;
    private final HashSet<String> subdivisions;
    private final int defcon;
    private final WeatherAlertTime time;
    private final EventSource source;

    public WeatherAlert(WeatherPreAlert preAlert, HashSet<WeatherZone> zones, EventSource source) {
        this.event = preAlert.getEvent();
        this.defcon = preAlert.getDefcon();
        this.subdivisions = preAlert.getSubdivisions();
        this.certainty = preAlert.getCertainty();
        this.headline = preAlert.getHeadline();
        this.instruction = preAlert.getInstruction();
        this.description = preAlert.getDescription();
        this.time = preAlert.getTime();
        this.zones = zones;
        this.source = source;
    }

    public JSONObjectTranslatable toJSONObject() {
        final JSONObjectTranslatable json = new JSONObjectTranslatable("event", "headline", "instruction", "description");
        json.put("defcon", defcon);
        json.put("event", event);
        if(subdivisions != null) {
            json.put("subdivisions", new JSONArray(subdivisions));
        }
        json.put("certainty", certainty);
        json.put("headline", headline);
        if(instruction != null && !instruction.isEmpty()) {
            json.put("instruction", instruction);
        }
        json.put("description", description);
        json.put("time", time.toJSONObject());
        json.put("zones", new JSONArray(zones));
        json.put("source", source.toJSONObject());
        return json;
    }
}
