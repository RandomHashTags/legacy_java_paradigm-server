package me.randomhashtags.worldlaws.service;

import me.randomhashtags.worldlaws.Jsonable;

public enum NASAService {
    ;
    private static String API_KEY;

    public static String getNASA_APIKey() {
        if(API_KEY == null) {
            API_KEY = Jsonable.getSettingsPrivateValuesJSON().getJSONObject("nasa").getString("api_key");
        }
        return API_KEY;
    }
}
