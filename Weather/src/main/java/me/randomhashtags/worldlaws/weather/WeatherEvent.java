package me.randomhashtags.worldlaws.weather;

public final class WeatherEvent {
    private final String event;
    private final int defcon;

    public WeatherEvent(String event, int defcon) {
        this.event = event;
        this.defcon = defcon;
    }

    public String getEvent() {
        return event;
    }
    public int getDefcon() {
        return defcon;
    }

    @Override
    public String toString() {
        return "{" +
                "\"event\":\"" + event + "\"," +
                "\"defcon\":" + defcon +
                "}";
    }
}
