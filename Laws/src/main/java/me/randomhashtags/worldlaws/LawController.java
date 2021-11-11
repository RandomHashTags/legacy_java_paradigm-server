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
    public void getRecentActivity(APIVersion version, CompletionHandler handler) {
        if(recentActivity.containsKey(version)) {
            handler.handleString(recentActivity.get(version));
        } else {
            final AtomicLong started = new AtomicLong(System.currentTimeMillis());
            final CompletionHandler completionHandler = new CompletionHandler() {
                @Override
                public void handleString(String string) {
                    WLLogger.logInfo(getClass().getSimpleName() + " - refreshed recent activity (took " + (System.currentTimeMillis()-started.get()) + "ms)");
                    recentActivity.put(version, string);
                    handler.handleString(string);
                }
            };
            Laws.INSTANCE.registerFixedTimer(WLUtilities.LAWS_RECENT_ACTIVITY_UPDATE_INTERVAL, new CompletionHandler() {
                @Override
                public void handleObject(Object object) {
                    started.set(System.currentTimeMillis());
                    refreshRecentActivity(version, completionHandler);
                }
            });
            refreshRecentActivity(version, completionHandler);
        }
    }
    public abstract void refreshRecentActivity(APIVersion version, CompletionHandler handler);
    public abstract void getResponse(APIVersion version, String value, CompletionHandler handler);
    public abstract void getGovernmentResponse(APIVersion version, int administration, String value, CompletionHandler handler);
}
