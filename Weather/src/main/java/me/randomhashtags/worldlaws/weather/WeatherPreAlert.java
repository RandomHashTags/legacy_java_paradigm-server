package me.randomhashtags.worldlaws.weather;

import me.randomhashtags.worldlaws.LocalServer;

import java.util.HashSet;
import java.util.List;

public final class WeatherPreAlert {
    private final int defcon;
    private final String event, id, certainty, headline, instruction, description;
    private final List<String> subdivisions;
    private final HashSet<WeatherZone> zones;
    private final WeatherAlertTime time;

    public WeatherPreAlert(int defcon, String event, String id, List<String> subdivisions, String certainty, String headline, String instruction, String description, HashSet<WeatherZone> zones, WeatherAlertTime time) {
        this.defcon = defcon;
        this.event = event;
        this.id = id;
        this.subdivisions = subdivisions;
        this.certainty = LocalServer.fixEscapeValues(certainty);
        this.headline = LocalServer.fixEscapeValues(headline);
        this.instruction = LocalServer.fixEscapeValues(instruction);
        this.description = LocalServer.fixEscapeValues(description);
        this.zones = zones;
        this.time = time;
    }

    public WeatherPreAlert onlyWithSubdivision(String subdivision) {
        final HashSet<WeatherZone> subdivisionZones = new HashSet<>(zones);
        subdivisionZones.removeIf(zone -> !zone.getSubdivision().equalsIgnoreCase(subdivision));
        return new WeatherPreAlert(0, null, id, null, null, null, null, null, subdivisionZones, time);
    }

    public int getDefcon() {
        return defcon;
    }
    public String getEvent() {
        return event;
    }
    public List<String> getSubdivisions() {
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

    private String getAreas() {
        final StringBuilder builder = new StringBuilder("[");
        boolean isFirst = true;
        for(WeatherZone zone : zones) {
            builder.append(isFirst ? "" : ",").append("\"").append(zone.getName()).append("\"");
            isFirst = false;
        }
        builder.append("]");
        return builder.toString();
    }

    @Override
    public String toString() {
        return "\"" + id + "\":{" +
                "\"areas\":" + getAreas() + "," +
                "\"time\":" + time.toString() +
                "}";
    }
}
