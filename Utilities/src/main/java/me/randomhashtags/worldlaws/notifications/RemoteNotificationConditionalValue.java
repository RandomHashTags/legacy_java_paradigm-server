package me.randomhashtags.worldlaws.notifications;

import org.json.JSONObject;

public final class RemoteNotificationConditionalValue extends JSONObject {

    public RemoteNotificationConditionalValue(JSONObject json) {
        for(String key : json.keySet()) {
            put(key, json.get(key));
        }
    }
    public RemoteNotificationConditionalValue(String countryBackendID, String identifier, String value) {
        put("countryBackendID", countryBackendID);
        put("identifier", identifier);
        put("value", value);
    }

    public String getCountryBackendID() {
        return getString("countryBackendID");
    }
    public String getIdentifier() {
        return getString("identifier");
    }
    public String getValue() {
        return getString("value");
    }

    public String getFormattedValue() {
        return getCountryBackendID() + "|" + getIdentifier() + "|" + getValue();
    }
}
