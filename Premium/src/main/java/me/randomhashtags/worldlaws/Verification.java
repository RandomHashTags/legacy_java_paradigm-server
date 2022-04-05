package me.randomhashtags.worldlaws;

import me.randomhashtags.worldlaws.locale.JSONObjectTranslatable;
import me.randomhashtags.worldlaws.settings.Settings;
import org.json.JSONObject;

import java.util.LinkedHashMap;

public enum Verification implements RestAPI {
    INSTANCE;

    public JSONObjectTranslatable verifyAppleAutoRenewableSubscription(String value) {
        final boolean productionMode = Settings.PrivateValues.Apple.isProductionMode();
        final String sharedSecret = Settings.PrivateValues.Apple.getVerifySubscriptionSharedSecret();

        final String prefix = productionMode ? "buy" : "sandbox";
        final String url = "https://" + prefix + ".itunes.apple.com/verifyReceipt";
        final LinkedHashMap<String, String> headers = new LinkedHashMap<>(GET_CONTENT_HEADERS);
        headers.put("receipt-data", value);
        headers.put("exclude-old-transactions", "true");
        headers.put("password", sharedSecret);
        final JSONObject json = postJSONObject(url, null, true, headers);
        JSONObjectTranslatable translatable = null;
        int status = 0;
        if(json != null) {
            translatable = new JSONObjectTranslatable();
            for(String key : json.keySet()) {
                translatable.put(key, json.get(key));
            }
            status = json.getInt("status");
        }
        WLLogger.logInfo("Verification - status=" + status + ";json=" + translatable);
        return translatable;
    }
}
