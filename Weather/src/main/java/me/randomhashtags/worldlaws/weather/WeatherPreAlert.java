package me.randomhashtags.worldlaws.weather;

import me.randomhashtags.worldlaws.locale.JSONObjectTranslatable;
import org.json.JSONArray;

import java.util.HashSet;

public final class WeatherPreAlert {
    private final int defcon;
    private final String event, id, certainty, headline, instruction, description;
    private final HashSet<String> subdivisions;
    private final HashSet<WeatherZone> zones;
    private final WeatherAlertTime time;

    public WeatherPreAlert(int defcon, String event, String id, HashSet<String> subdivisions, String certainty, String headline, String instruction, String description, HashSet<WeatherZone> zones, WeatherAlertTime time) {
        this.defcon = defcon;
        this.event = event;
        this.id = id;
        this.subdivisions = subdivisions;
        this.certainty = certainty;
        this.headline = headline;
        this.instruction = instruction;
        this.description = description;
        this.zones = zones;
        this.time = time;
    }

    public WeatherPreAlert onlyWithSubdivision(String subdivision) {
        final HashSet<WeatherZone> subdivisionZones = new HashSet<>(zones);
        subdivisionZones.removeIf(zone -> !zone.getSubdivision().equalsIgnoreCase(subdivision));
        final HashSet<String> subdivisions = new HashSet<>() {{ add(subdivision); }};
        return new WeatherPreAlert(0, null, id, subdivisions, null, null, null, null, subdivisionZones, time);
    }

    public String getID() {
        return id;
    }
    public int getDefcon() {
        return defcon;
    }
    public String getEvent() {
        return event;
    }
    public HashSet<String> getSubdivisions() {
        return subdivisions;
    }
    public String getCertainty() {
        return certainty;
    }
    public String getHeadline() {
        return headline;
    }
    public String getInstruction() {
        return instruction;
    }
    public String getDescription() {
        return description;
    }
    public HashSet<WeatherZone> getZones() {
        return zones;
    }
    public WeatherAlertTime getTime() {
        return time;
    }

    private JSONArray getAreas() {
        final JSONArray array = new JSONArray();
        for(WeatherZone zone : zones) {
            array.put(zone.getName());
        }
        return array;
    }

    public JSONObjectTranslatable toJSONObject() {
        final JSONObjectTranslatable json = new JSONObjectTranslatable();
        json.put("areas", getAreas());
        json.put("time", time.toJSONObject());
        return json;
    }
}
