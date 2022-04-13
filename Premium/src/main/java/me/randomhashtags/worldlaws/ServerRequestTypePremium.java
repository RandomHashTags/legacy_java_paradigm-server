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

    @Override
    public boolean isConditional() {
        return this == VERIFY;
    }

    private JSONObjectTranslatable verifyApple(String[] values, WLHttpExchange request) {
        if(request != null && request.getActualRequestMethod() == RequestMethod.POST) {
            final JSONObject json = request.getRequestBodyJSON();
            if(json != null) {
                switch (values[1]) {
                    case "v2":
                        return Verification.INSTANCE.verifyResponseBodyV2(json);
                    case "receipt":
                        return Verification.INSTANCE.verifyReceipt(request.isSandbox(), null);
                    default:
                        return null;
                }
            }
        }
        return null;
    }
}
