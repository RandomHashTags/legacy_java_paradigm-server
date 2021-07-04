package me.randomhashtags.worldlaws.country;

import me.randomhashtags.worldlaws.APIVersion;
import me.randomhashtags.worldlaws.CompletionHandler;
import me.randomhashtags.worldlaws.location.WLCountry;

public interface LawController {
    WLCountry getCountry();
    void getRecentActivity(APIVersion version, CompletionHandler handler);
    void getResponse(APIVersion version, String value, CompletionHandler handler);
}
