package me.randomhashtags.worldlaws.weather.recode;

import me.randomhashtags.worldlaws.LocalServer;
import me.randomhashtags.worldlaws.weather.WeatherAlertTime;

import java.util.HashSet;

public final class NewWeatherPreAlert {

    private final String event, id, country, territory, severity, certainty, headline, instruction, description;
    private final HashSet<String> zoneIDs;
    private final WeatherAlertTime time;

    public NewWeatherPreAlert(String event, String id, String country, String territory, String severity, String certainty, String headline, String instruction, String description, HashSet<String> zoneIDs, WeatherAlertTime time) {
        this.event = event;
        this.id = id;
        this.country = country;
        this.territory = territory;
        this.severity = LocalServer.fixEscapeValues(severity);
        this.certainty = LocalServer.fixEscapeValues(certainty);
        this.headline = LocalServer.fixEscapeValues(headline);
        this.instruction = LocalServer.fixEscapeValues(instruction);
        this.description = LocalServer.fixEscapeValues(description);
        this.zoneIDs = zoneIDs;
        this.time = time;
    }

    public String getEvent() {
        return event;
    }
    public String getID() {
        return id;
    }
    public String getCountry() {
        return country;
    }
    public String getTerritory() {
        return territory;
    }
    public String getSeverity() {
        return severity;
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

    private String getZonesJSON() {
        final StringBuilder builder = new StringBuilder("[");
        boolean isFirst = true;
        for(String string : zoneIDs) {
            builder.append(isFirst ? "" : ",").append("\"").append(string).append("\"");
            isFirst = false;
        }
        return builder.append("]").toString();
    }

    @Override
    public String toString() {
        return "{" +
                "\"id\":\"" + id + "\"," +
                "\"territory\":\"" + territory + "\"," +
                "\"zones\":" + getZonesJSON() + "," +
                "\"time\":" + time.toString() +
                "}";
    }
}
