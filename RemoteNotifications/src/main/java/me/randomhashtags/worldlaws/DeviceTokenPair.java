package me.randomhashtags.worldlaws;

import org.json.JSONArray;

import java.util.HashSet;
import java.util.List;

public final class DeviceTokenPair {

    private final String deviceToken;
    private final HashSet<String> values;


    public DeviceTokenPair(String deviceToken, String value) {
        this(deviceToken, new HashSet<>(List.of(value)));
    }
    public DeviceTokenPair(String deviceToken, HashSet<String> values) {
        this.deviceToken = deviceToken;
        this.values = values;
    }

    public String getDeviceToken() {
        return deviceToken;
    }
    public HashSet<String> getValues() {
        return values;
    }
    public void addValue(String value) {
        values.add(value);
    }
    public void removeValue(String value) {
        values.remove(value);
    }

    public JSONArray toJSONArray() {
        return new JSONArray(values);
    }
}
