package me.randomhashtags.worldlaws;

import me.randomhashtags.worldlaws.settings.Settings;
import org.json.JSONObject;

import java.util.HashMap;

public enum Verification implements RestAPI {
    INSTANCE;

    public void verifyApple(String value) {
        final boolean productionMode = Settings.PrivateValues.Apple.isProductionMode();
        final String sharedSecret = Settings.PrivateValues.Apple.getSharedSecret();

        final String prefix = productionMode ? "buy" : "sandbox";
        final String url = "https://" + prefix + ".itunes.apple.com/verifyReceipt";
        final HashMap<String, String> headers = new HashMap<>(CONTENT_HEADERS);
        headers.put("receipt-data", value);
        headers.put("password", sharedSecret);
        final JSONObject json = requestJSONObject(url, RequestMethod.POST, headers);
        final int status = json.getInt("status");
    }
}
