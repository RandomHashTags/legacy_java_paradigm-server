package me.randomhashtags.worldlaws.weather;

import me.randomhashtags.worldlaws.LocalServer;

import java.util.HashSet;

public final class WeatherPreAlert {
    private final int defcon;
    private final String event, id, subdivision, certainty, headline, instruction, description;
    private final HashSet<String> zoneIDs;
    private final WeatherAlertTime time;

    public WeatherPreAlert(int defcon, String event, String id, String subdivision, String certainty, String headline, String instruction, String description, HashSet<String> zoneIDs, WeatherAlertTime time) {
        this.defcon = defcon;
        this.event = event;
        this.id = id;
        this.subdivision = subdivision;
        this.certainty = LocalServer.fixEscapeValues(certainty);
        this.headline = LocalServer.fixEscapeValues(headline);
        this.instruction = LocalServer.fixEscapeValues(instruction);
        this.description = LocalServer.fixEscapeValues(description);
        this.zoneIDs = zoneIDs;
        this.time = time;
    }

    public int getDefcon() {
        return defcon;
    }
    public String getEvent() {
        return event;
    }
    public String getID() {
        return id;
    }
    public String getSubdivision() {
        return subdivision;
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
    public HashSet<String> getZoneIDs() {
        return zoneIDs;
    }
    public WeatherAlertTime getTime() {
        return time;
    }

    @Override
    public String toString() {
        return "\"" + id + "\":{" +
                "\"time\":" + time.toString() +
                "}";
    }
}
