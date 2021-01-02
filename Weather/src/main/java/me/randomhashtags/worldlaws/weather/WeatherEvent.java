package me.randomhashtags.worldlaws.weather;

public final class WeatherEvent {

    private String event;
    private int defcon;

    public WeatherEvent(String event, int defcon) {
        this.event = event;
        this.defcon = defcon;
    }

    @Override
    public String toString() {
        return "{" +
                "\"event\":\"" + event + "\"," +
                "\"defcon\":" + defcon +
                "}";
    }
}
