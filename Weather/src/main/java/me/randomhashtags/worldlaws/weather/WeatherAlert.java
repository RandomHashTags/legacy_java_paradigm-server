package me.randomhashtags.worldlaws.weather;

import me.randomhashtags.worldlaws.LocalServer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class WeatherAlert {
    private final String territory, severity, certainty, event, headline, description, instruction;
    private String area;
    private final List<String> areas;
    private final WeatherAlertTime time;

    public WeatherAlert(String territory, String area, String severity, String certainty, String event, String headline, String description, String instruction, WeatherAlertTime time) {
        this.territory = territory;
        this.area = area;
        this.areas = new ArrayList<>(Arrays.asList(area));
        this.severity = severity;
        this.certainty = certainty;
        this.event = LocalServer.fixEscapeValues(event);
        this.headline = LocalServer.fixEscapeValues(headline);
        this.description = LocalServer.fixEscapeValues(description);
        this.instruction = LocalServer.fixEscapeValues(instruction);
        this.time = time;
    }

    public String getTerritory() {
        return territory;
    }

    public String getArea() {
        return area;
    }
    public void addArea(String area) {
        areas.add(area);
        updateArea();
    }
    private void updateArea() {
        final StringBuilder builder = new StringBuilder();
        boolean isFirst = true;
        for(String area : areas) {
            builder.append(isFirst ? "" : ", ").append(area);
            isFirst = false;
        }
        area = builder.toString();
    }

    public String getSeverity() {
        return severity;
    }
    public String getCertainty() {
        return certainty;
    }
    public String getEvent() {
        return event;
    }
    public String getHeadline() {
        return headline;
    }
    public String getDescription() {
        return description;
    }
    public String getInstruction() {
        return instruction;
    }

    @Override
    public String toString() {
        return "{\"properties\":{" +
                "\"territory\":\"" + territory + "\"," +
                "\"area\":\"" + area + "\"," +
                "\"severity\":\"" + severity + "\"," +
                "\"certainty\":\"" + certainty + "\"," +
                "\"event\":\"" + event + "\"," +
                "\"headline\":\"" + headline + "\"," +
                "\"description\":\"" + description + "\"," +
                (instruction != null ? "\"instruction\":\"" + instruction + "\"," : "") +
                "\"time\":" + time.toString() +
                "}}";
    }
}
