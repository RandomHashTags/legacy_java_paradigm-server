package me.randomhashtags.worldlaws;

import me.randomhashtags.worldlaws.locale.JSONObjectTranslatable;
import me.randomhashtags.worldlaws.settings.Settings;
import org.json.JSONObject;

import java.util.LinkedHashMap;

public enum Verification implements RestAPI {
    INSTANCE;

    public JSONObjectTranslatable verifyResponseBodyV2(JSONObject json) {
        WLLogger.logInfo("Verification;verifyResponseBodyV2;json=" + json.toString());
        return null;
    }

    public JSONObjectTranslatable verifyReceipt(boolean sandbox, String value) {
        return verifyReceipt(sandbox, value, false);
    }
    private JSONObjectTranslatable verifyReceipt(boolean sandbox, String value, boolean checkRecursion) {
        final String sharedSecret = Settings.PrivateValues.Apple.getVerifySubscriptionSharedSecret();
        final String prefix = sandbox ? "sandbox" : "buy";
        final String url = "https://" + prefix + ".itunes.apple.com/verifyReceipt";

        final LinkedHashMap<String, Object> postData = new LinkedHashMap<>();
        postData.put("receipt-data", value);
        postData.put("password", sharedSecret);
        postData.put("exclude-old-transactions", true);

        final JSONObject json = postJSONObject(url, postData, true, null);
        if(json != null) {
            final int status = json.getInt("status");
            if(status == 21007) {
                return checkRecursion ? null : verifyReceipt(!sandbox, value, true);
            }
            return JSONObjectTranslatable.copy(json);
        }
        return null;
    }
}
