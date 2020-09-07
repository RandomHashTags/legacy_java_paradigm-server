package me.randomhashtags.worldlaws.weather;

import me.randomhashtags.worldlaws.LocalServer;

import java.time.LocalDateTime;
import java.util.*;

public final class WeatherAlert {
    private String territory, area, severity, certainty, event, headline, description, instruction;
    private List<String> areas;
    private LocalDateTime sent, effective, expires, ends;

    public WeatherAlert(String territory, String area, String severity, String certainty, String event, String headline, String description, String instruction) {
        this.territory = territory;
        this.area = area;
        this.areas = new ArrayList<>(Arrays.asList(area));
        this.severity = severity;
        this.certainty = certainty;
        this.event = event;
        this.headline = LocalServer.fixEscapeValues(headline);
        this.description = LocalServer.fixEscapeValues(description);
        this.instruction = LocalServer.fixEscapeValues(instruction);
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

                "\"time\":{" +
                //"\"sent\":\"" + sent.toString() + "\"," +
                //"\"effective\":\"" + effective.toString() + "\"," +
                //"\"expires\":\"" + expires.toString() + "\"," +
                //"\"ends\":\"" + ends.toString() + "\"" +
                "}" +

                "" +
                "}}";
    }
}
