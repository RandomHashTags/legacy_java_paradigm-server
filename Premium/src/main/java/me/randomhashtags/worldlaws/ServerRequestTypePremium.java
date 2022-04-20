package me.randomhashtags.worldlaws;

import me.randomhashtags.worldlaws.locale.JSONObjectTranslatable;
import me.randomhashtags.worldlaws.request.ServerRequestType;
import me.randomhashtags.worldlaws.request.WLHttpExchange;
import me.randomhashtags.worldlaws.request.WLHttpHandler;
import org.json.JSONObject;

public enum ServerRequestTypePremium implements ServerRequestType {
    VERIFY,
    ;

    @Override
    public WLHttpHandler getHandler(APIVersion version) {
        return httpExchange -> {
            switch (this) {
                case VERIFY:
                    final String[] values = httpExchange.getShortPathValues();
                    final String key = values[0];
                    switch (key) {
                        case "apple":
                            return verifyApple(values, httpExchange);
                        default:
                            return null;
                    }
                default:
                    return null;
            }
        };
    }

    private JSONObjectTranslatable verifyApple(String[] values, WLHttpExchange request) {
        if(request != null && request.getActualRequestMethod() == RequestMethod.POST) {
            final JSONObject json = request.getRequestBodyJSON();
            if(json != null) {
                switch (values[1]) {
                    case "v2":
                        return Verification.INSTANCE.verifyResponseBodyV2(json);
                    case "receipt":
                        final String receiptData = json.optString("receipt-data", null);
                        return receiptData != null ? Verification.INSTANCE.verifyReceipt(request.isSandbox(), receiptData) : null;
                    default:
                        return null;
                }
            }
        }
        return null;
    }
}
