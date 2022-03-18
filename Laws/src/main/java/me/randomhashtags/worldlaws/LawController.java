package me.randomhashtags.worldlaws;

import me.randomhashtags.worldlaws.country.WLCountry;
import me.randomhashtags.worldlaws.locale.JSONObjectTranslatable;

import java.util.HashMap;

public abstract class LawController {

    private long started;
    private final HashMap<APIVersion, JSONObjectTranslatable> recentActivity;

    public LawController() {
        recentActivity = new HashMap<>();
    }

    public abstract WLCountry getCountry();
    public abstract BillStatus[] getBillStatuses();
    public int getCurrentAdministrationVersion() {
        return LawUtilities.getCurrentAdministrationVersion(getCountry());
    }
    public JSONObjectTranslatable getRecentActivity(APIVersion version) {
        if(!recentActivity.containsKey(version)) {
            started = System.currentTimeMillis();
            final String simpleName = getClass().getSimpleName();
            Laws.INSTANCE.registerFixedTimer(UpdateIntervals.Laws.RECENT_ACTIVITY, () -> {
                started = System.currentTimeMillis();
                final JSONObjectTranslatable json = refreshRecentActivity(version);
                loadedRecentActivity(version, simpleName, json);
            });
            final JSONObjectTranslatable json = refreshRecentActivity(version);
            loadedRecentActivity(version, simpleName, json);
        }
        return recentActivity.get(version);
    }
    private void loadedRecentActivity(APIVersion version, String simpleName, JSONObjectTranslatable json) {
        WLLogger.logInfo(simpleName + " - refreshed recent activity (took " + WLUtilities.getElapsedTime(started) + ")");
        recentActivity.put(version, json);
    }
    public abstract JSONObjectTranslatable refreshRecentActivity(APIVersion version);
    public abstract JSONObjectTranslatable getResponse(APIVersion version, String value);
    public abstract JSONObjectTranslatable getGovernmentResponse(APIVersion version, int administration, String value);
}
