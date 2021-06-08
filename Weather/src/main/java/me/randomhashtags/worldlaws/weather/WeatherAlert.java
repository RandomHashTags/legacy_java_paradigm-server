package me.randomhashtags.worldlaws.weather;

import me.randomhashtags.worldlaws.EventSource;

public final class WeatherAlert {
    private final String event, certainty, headline, instruction, description, zones;
    private final int defcon;
    private final WeatherAlertTime time;
    private final EventSource source;

    public WeatherAlert(WeatherPreAlert preAlert, String zones, EventSource source) {
        this.event = preAlert.getEvent();
        this.defcon = preAlert.getDefcon();
        this.certainty = preAlert.getCertainty();
        this.headline = preAlert.getHeadline();
        this.instruction = preAlert.getInstruction();
        this.description = preAlert.getDescription();
        this.time = preAlert.getTime();
        this.zones = zones;
        this.source = source;
    }

    @Override
    public String toString() {
        return "{" +
                "\"defcon\":" + defcon + "," +
                "\"event\":\"" + event + "\"," +
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
