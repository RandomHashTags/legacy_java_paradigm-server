package me.randomhashtags.worldlaws.weather.recode;

import me.randomhashtags.worldlaws.EventSource;

public final class NewWeatherAlert {

    private final String event, severity, certainty, headline, instruction, description, zones;
    private final EventSource source;

    public NewWeatherAlert(NewWeatherPreAlert preAlert, String zones, EventSource source) {
        this.event = preAlert.getEvent();
        this.severity = preAlert.getSeverity();
        this.certainty = preAlert.getCertainty();
        this.headline = preAlert.getHeadline();
        this.instruction = preAlert.getInstruction();
        this.description = preAlert.getDescription();
        this.zones = zones;
        this.source = source;
    }

    @Override
    public String toString() {
        return "{" +
                "\"event\":\"" + event + "\"," +
                "\"severity\":\"" + severity + "\"," +
                "\"certainty\":\"" + certainty + "\"," +
                "\"headline\":\"" + headline + "\"," +
                "\"instruction\":\"" + instruction + "\"," +
                "\"description\":\"" + description + "\"," +
                "\"zones\":" + zones + "," +
                "\"source\":" + source.toString() +
                "}";
    }
}
