package me.randomhashtags.worldlaws;

import me.randomhashtags.worldlaws.country.WLCountry;
import me.randomhashtags.worldlaws.law.LawUtilities;
import org.apache.logging.log4j.Level;

import java.util.HashMap;

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
            final long started = System.currentTimeMillis();
            loadRecentActivity(version, new CompletionHandler() {
                @Override
                public void handleString(String string) {
                    WLLogger.log(Level.INFO, getClass().getSimpleName() + " - loaded recent activity (took " + (System.currentTimeMillis()-started) + "ms)");
                    recentActivity.put(version, string);
                    handler.handleString(string);
                }
            });
        }
    }
    public abstract void loadRecentActivity(APIVersion version, CompletionHandler handler);
    public abstract void getResponse(APIVersion version, String value, CompletionHandler handler);
    public abstract void getGovernmentResponse(APIVersion version, int administration, String value, CompletionHandler handler);
}
