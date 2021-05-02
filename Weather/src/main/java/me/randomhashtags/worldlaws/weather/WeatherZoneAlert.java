package me.randomhashtags.worldlaws.weather;

import java.util.HashSet;

public final class WeatherZoneAlert {
    private final int defcon;
    private final String id, event;
    private final HashSet<String> zoneIDs;
    private final WeatherAlertTime time;

    public WeatherZoneAlert(int defcon, String id, String event, HashSet<String> zoneIDs, WeatherAlertTime time) {
        this.defcon = defcon;
        this.id = id;
        this.event = event;
        this.zoneIDs = zoneIDs;
        this.time = time;
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
                "\"defcon\":" + defcon + "," +
                "\"id\":\"" + id + "\"," +
                "\"event\":\"" + event + "\"," +
                "\"zones\":" + getZonesJSON() + "," +
                "\"time\":" + time.toString() +
                "}";
    }
}
