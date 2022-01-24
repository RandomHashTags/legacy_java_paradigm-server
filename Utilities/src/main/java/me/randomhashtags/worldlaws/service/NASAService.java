package me.randomhashtags.worldlaws.service;

import me.randomhashtags.worldlaws.settings.Settings;

public enum NASAService {
    ;

    public static String getNASA_APIKey() {
        return Settings.PrivateValues.NASA.getAPIKey();
    }
}
