package me.randomhashtags.worldlaws;

import me.randomhashtags.worldlaws.country.WLCountry;
import me.randomhashtags.worldlaws.law.LawUtilities;

public interface LawController {
    WLCountry getCountry();
    BillStatus[] getBillStatuses();
    default int getCurrentAdministrationVersion() {
        return LawUtilities.getCurrentAdministrationVersion(getCountry());
    }
    void getRecentActivity(APIVersion version, CompletionHandler handler);
    void getResponse(APIVersion version, String value, CompletionHandler handler);
    void getGovernmentResponse(APIVersion version, int administration, String value, CompletionHandler handler);
}
