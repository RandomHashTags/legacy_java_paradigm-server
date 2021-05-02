package me.randomhashtags.worldlaws.weather;

import me.randomhashtags.worldlaws.LocalServer;
import me.randomhashtags.worldlaws.weather.WeatherAlertTime;

import java.util.HashSet;

public final class WeatherPreAlert {
    private final int defcon;
    private final String event, id, territory, certainty, headline, instruction, description;
    private final HashSet<String> zoneIDs;
    private final WeatherAlertTime time;

    public WeatherPreAlert(int defcon, String event, String id, String territory, String certainty, String headline, String instruction, String description, HashSet<String> zoneIDs, WeatherAlertTime time) {
        this.defcon = defcon;
        this.event = event;
        this.id = id;
        this.territory = territory;
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
    public String getTerritory() {
        return territory;
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
        return "{" +
                "\"id\":\"" + id + "\"," +
                "\"territory\":\"" + territory + "\"," +
                "\"time\":" + time.toString() +
                "}";
    }
}
