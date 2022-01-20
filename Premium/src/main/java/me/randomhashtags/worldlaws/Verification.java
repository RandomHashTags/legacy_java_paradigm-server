package me.randomhashtags.worldlaws;

import org.json.JSONObject;

import java.util.HashMap;

public enum Verification implements RestAPI {
    INSTANCE;

    private final boolean appleProductionMode;
    private final String appleSharedSecret;

    Verification() {
        final JSONObject json = Jsonable.getSettingsPrivateValuesJSON().getJSONObject("apple");
        appleProductionMode = json.getBoolean("production_mode");
        appleSharedSecret = json.getString("app_specific_shared_secret");
    }

    public void verifyApple(String value) {
        final String prefix = appleProductionMode ? "buy" : "sandbox";
        final String url = "https://" + prefix + ".itunes.apple.com/verifyReceipt";
        final HashMap<String, String> headers = new HashMap<>(CONTENT_HEADERS);
        headers.put("receipt-data", value);
        headers.put("password", appleSharedSecret);
        final JSONObject json = requestJSONObject(url, RequestMethod.POST, headers);
        final int status = json.getInt("status");
    }
}