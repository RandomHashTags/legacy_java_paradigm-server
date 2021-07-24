package me.randomhashtags.worldlaws.country;

import me.randomhashtags.worldlaws.APIVersion;
import me.randomhashtags.worldlaws.CompletionHandler;
import me.randomhashtags.worldlaws.law.LawUtilities;
import me.randomhashtags.worldlaws.location.WLCountry;

public interface LawController {
    WLCountry getCountry();
    default int getCurrentAdministrationVersion() {
        return LawUtilities.getCurrentAdministrationVersion(getCountry());
    }
    void getRecentActivity(APIVersion version, CompletionHandler handler);
    void getResponse(APIVersion version, String value, CompletionHandler handler);
    void getGovernmentResponse(APIVersion version, int administration, String value, CompletionHandler handler);
}
