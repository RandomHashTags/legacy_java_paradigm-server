package me.randomhashtags.worldlaws;

import me.randomhashtags.worldlaws.locale.JSONObjectTranslatable;
import me.randomhashtags.worldlaws.settings.Settings;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.LinkedHashMap;

public enum Verification implements RestAPI {
    INSTANCE;

    public JSONObjectTranslatable verifyResponseBodyV2(JSONObject json) {
        return null;
    }

    public JSONObjectTranslatable verifyReceipt(boolean sandbox, String value) {
        final long nowMilliseconds = System.currentTimeMillis();
        final String sharedSecret = Settings.PrivateValues.Apple.getVerifySubscriptionSharedSecret();

        final String prefix = sandbox ? "sandbox" : "buy";
        final String url = "https://" + prefix + ".itunes.apple.com/verifyReceipt";
        final LinkedHashMap<String, String> headers = new LinkedHashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put("receipt-data", value);
        headers.put("exclude-old-transactions", "true");

        final LinkedHashMap<String, Object> postData = new LinkedHashMap<>();
        postData.put("receipt-data", value);
        postData.put("password", sharedSecret);
        postData.put("exclude-old-transactions", "true");

        final JSONObject json = postJSONObject(url, postData, headers);
        JSONObjectTranslatable translatable = null;
        int status = 0;
        if(json != null) {
            status = json.getInt("status");
            final JSONArray latestReceiptInfo = json.optJSONArray("latest_receipt_info");
            if(latestReceiptInfo != null) {

            }
            translatable = new JSONObjectTranslatable();
            for(String key : json.keySet()) {
                translatable.put(key, json.get(key));
            }
        }
        WLLogger.logInfo("Verification - status=" + status + ";json=" + translatable);
        return translatable;
    }
}
