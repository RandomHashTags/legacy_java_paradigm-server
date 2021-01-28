package me.randomhashtags.worldlaws.weather.recode;

public final class NewWeatherAlert {

    private final String event, severity, certainty, headline, instruction, description, zones;

    public NewWeatherAlert(NewWeatherPreAlert preAlert, String zones) {
        this.event = preAlert.getEvent();
        this.severity = preAlert.getSeverity();
        this.certainty = preAlert.getCertainty();
        this.headline = preAlert.getHeadline();
        this.instruction = preAlert.getInstruction();
        this.description = preAlert.getDescription();
        this.zones = zones;
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
                "\"zones\":" + zones +
                "}";
    }
}
