package me.randomhashtags.worldlaws.location;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;

public final class UTCOffset {
    private final int hour, minute;
    private final String regions;

    public UTCOffset(int hour, int minute, String regions) {
        this.hour = hour;
        this.minute = minute;
        this.regions = regions;
    }
    public UTCOffset(JSONObject json) {
        hour = json.getInt("hour");
        minute = json.getInt("minute");
        this.regions = json.getString("regions");
    }

    public int getHour() {
        return hour;
    }
    public int getMinute() {
        return minute;
    }
    public String getRegions() {
        return regions;
    }

    @Override
    public String toString() {
        return "{" +
                "\"hour\":" + hour + "," +
                "\"minute\":" + minute + "," +
                "\"regions\":\"" + regions + "\"" +
                "}";
    }
}
