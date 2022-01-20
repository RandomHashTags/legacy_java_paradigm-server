package me.randomhashtags.worldlaws.weather;

import me.randomhashtags.worldlaws.EventSource;

import java.util.List;

public final class WeatherAlert {
    private final String event, certainty, headline, instruction, description, zones;
    private final List<String> subdivisions;
    private final int defcon;
    private final WeatherAlertTime time;
    private final EventSource source;

    public WeatherAlert(WeatherPreAlert preAlert, String zones, EventSource source) {
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

    private String getSubdivisionsArray() {
        final StringBuilder builder = new StringBuilder("[");
        boolean isFirst = true;
        for(String subdivision : subdivisions) {
            builder.append(isFirst ? "" : ",").append("\"").append(subdivision).append("\"");
            isFirst = false;
        }
        builder.append("]");
        return builder.toString();
    }

    @Override
    public String toString() {
        return "{" +
                "\"defcon\":" + defcon + "," +
                "\"event\":\"" + event + "\"," +
                (subdivisions != null ? "\"subdivisions\":" + getSubdivisionsArray() + "," : "") +
                "\"certainty\":\"" + certainty + "\"," +
                "\"headline\":\"" + headline + "\"," +
                (instruction != null && !instruction.isEmpty() ? "\"instruction\":\"" + instruction + "\"," : "") +
                "\"description\":\"" + description + "\"," +
                "\"time\":" + time.toString() + "," +
                "\"zones\":" + zones + "," +
                "\"source\":{" + source.toString() + "}" +
                "}";
    }
}
