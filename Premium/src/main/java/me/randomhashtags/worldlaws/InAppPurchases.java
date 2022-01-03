package me.randomhashtags.worldlaws;

import java.util.HashMap;

public enum InAppPurchases {
    ;
    private static final HashMap<APIVersion, String> CACHE = new HashMap<>();

    public static void getResponse(APIVersion apiVersion, CompletionHandler handler) {
        if(CACHE.containsKey(apiVersion)) {
            handler.handleString(CACHE.get(apiVersion));
        } else {
            final String string = Jsonable.getStaticLocalFileString(Folder.OTHER, "premium", "json");
            CACHE.put(apiVersion, string);
        }
    }
}
