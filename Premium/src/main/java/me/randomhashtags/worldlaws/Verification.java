package me.randomhashtags.worldlaws;

import me.randomhashtags.worldlaws.settings.Settings;
import org.json.JSONObject;

import java.util.HashMap;

public enum Verification implements RestAPI {
    INSTANCE;

    public String verifyAppleAutoRenewableSubscription(String value) {
        final boolean productionMode = Settings.PrivateValues.Apple.isProductionMode();
        final String sharedSecret = Settings.PrivateValues.Apple.getVerifySubscriptionSharedSecret();

        final String prefix = productionMode ? "buy" : "sandbox";
        final String url = "https://" + prefix + ".itunes.apple.com/verifyReceipt";
        final HashMap<String, String> headers = new HashMap<>(CONTENT_HEADERS);
        headers.put("receipt-data", value);
        headers.put("exclude-old-transactions", "true");
        headers.put("password", sharedSecret);
        final JSONObject json = requestJSONObject(url, RequestMethod.POST, headers);
        String string = null;
        int status = 0;
        if(json != null) {
            string = json.toString();
            status = json.getInt("status");
        }
        WLLogger.logInfo("Verification - status=" + status + ";json=" + string);
        return string;
    }
}
