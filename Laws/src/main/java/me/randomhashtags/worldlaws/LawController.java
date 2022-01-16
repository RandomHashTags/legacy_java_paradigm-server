package me.randomhashtags.worldlaws;

import me.randomhashtags.worldlaws.country.WLCountry;
import me.randomhashtags.worldlaws.law.LawUtilities;

import java.util.HashMap;
import java.util.concurrent.atomic.AtomicLong;

public abstract class LawController {

    private final HashMap<APIVersion, String> recentActivity;

    public LawController() {
        recentActivity = new HashMap<>();
    }

    public abstract WLCountry getCountry();
    public abstract BillStatus[] getBillStatuses();
    public int getCurrentAdministrationVersion() {
        return LawUtilities.getCurrentAdministrationVersion(getCountry());
    }
    public String getRecentActivity(APIVersion version) {
        if(!recentActivity.containsKey(version)) {
            final AtomicLong started = new AtomicLong(System.currentTimeMillis());
            final String simpleName = getClass().getSimpleName();
            final CompletionHandler completionHandler = new CompletionHandler() {
                @Override
                public void handleString(String string) {
                    WLLogger.logInfo(simpleName + " - refreshed recent activity (took " + (System.currentTimeMillis()-started.get()) + "ms)");
                    recentActivity.put(version, string);
                }
            };
            Laws.INSTANCE.registerFixedTimer(WLUtilities.LAWS_RECENT_ACTIVITY_UPDATE_INTERVAL, new CompletionHandler() {
                @Override
                public void handleObject(Object object) {
                    started.set(System.currentTimeMillis());
                    final String string = refreshRecentActivity(version);
                    completionHandler.handleString(string);
                }
            });
            final String string = refreshRecentActivity(version);
            completionHandler.handleString(string);
        }
        return recentActivity.get(version);
    }
    public abstract String refreshRecentActivity(APIVersion version);
    public abstract String getResponse(APIVersion version, String value);
    public abstract String getGovernmentResponse(APIVersion version, int administration, String value);
}
