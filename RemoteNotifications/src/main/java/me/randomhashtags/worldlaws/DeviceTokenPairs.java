package me.randomhashtags.worldlaws;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashSet;

public final class DeviceTokenPairs extends HashSet<DeviceTokenPair> {

    public DeviceTokenPairs() {
        super();
    }
    public DeviceTokenPairs(JSONObject json) {
        super();
        for(String key : json.keySet()) {
            final JSONArray array = json.getJSONArray(key);
            final HashSet<String> values = new HashSet<>();
            for(Object obj : array) {
                values.add((String) obj);
            }
            final DeviceTokenPair pair = new DeviceTokenPair(key, values);
            add(pair);
        }
    }

    public boolean removePairWithDeviceToken(String deviceToken) {
        final DeviceTokenPair pair = valueOfToken(deviceToken);
        if(pair != null) {
            remove(pair);
            return true;
        }
        return false;
    }
    public DeviceTokenPair valueOfToken(String deviceToken) {
        for(DeviceTokenPair pair : this) {
            if(pair.getDeviceToken().equals(deviceToken)) {
                return pair;
            }
        }
        return null;
    }
}
