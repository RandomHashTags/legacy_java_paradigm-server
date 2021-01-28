package me.randomhashtags.worldlaws.weather.recode;

public final class NewWeatherEvent {
    private final String event;
    private final int defcon;

    public NewWeatherEvent(String event, int defcon) {
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
