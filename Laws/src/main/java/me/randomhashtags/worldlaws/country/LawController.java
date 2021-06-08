package me.randomhashtags.worldlaws.country;

import me.randomhashtags.worldlaws.CompletionHandler;
import me.randomhashtags.worldlaws.location.WLCountry;

public interface LawController {
    WLCountry getCountry();
    void getRecentActivity(CompletionHandler handler);
    void getResponse(String value, CompletionHandler handler);
}
