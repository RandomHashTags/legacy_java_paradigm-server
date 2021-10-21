package me.randomhashtags.worldlaws.iap;

import me.randomhashtags.worldlaws.APIVersion;
import me.randomhashtags.worldlaws.LocalServer;
import org.json.JSONObject;

import java.util.HashMap;

public enum InAppPurchases {
    FAMILY_PREMIUM_ONE_MONTH,
    INDIVIDUAL_PREMIUM_ONE_MONTH,
    ;

    private static final HashMap<APIVersion, String> CACHE = new HashMap<>();

    public static String getProductIDs(APIVersion apiVersion) {
        if(CACHE.containsKey(apiVersion)) {
            return CACHE.get(apiVersion);
        } else {
            final JSONObject productsJSON = new JSONObject();
            for(InAppPurchases iap : InAppPurchases.values()) {
                final String type = iap.getType().getName();
                final JSONObject typeJSON = productsJSON.has(type) ? productsJSON.getJSONObject(type) : new JSONObject();

                final String category = iap.getCategory().name();
                final JSONObject json = typeJSON.has(category) ? typeJSON.getJSONObject(category) : new JSONObject();

                final String name = LocalServer.toCorrectCapitalization(iap.name()).replace(" ", "");
                final String productID = "me.randomhashtags.Paradigm." + name;
                final JSONObject productJSON = new JSONObject();
                json.put(productID, productJSON);

                typeJSON.put(category, json);
                productsJSON.put(type, typeJSON);
            }
            final String string = productsJSON.toString();
            CACHE.put(apiVersion, string);
            return string;
        }
    }

    public InAppPurchaseType getType() {
        switch (this) {
            case INDIVIDUAL_PREMIUM_ONE_MONTH:
            case FAMILY_PREMIUM_ONE_MONTH:
                return InAppPurchaseType.AUTO_RENEWING_SUBSCRIPTION;
            default:
                return InAppPurchaseType.UNKNOWN;
        }
    }
    public InAppPurchaseCategory getCategory() {
        switch (this) {
            case INDIVIDUAL_PREMIUM_ONE_MONTH:
                return InAppPurchaseCategory.INDIVIDUAL;
            case FAMILY_PREMIUM_ONE_MONTH:
                return InAppPurchaseCategory.FAMILY;
            default:
                return InAppPurchaseCategory.UNKNOWN;
        }
    }
}
