package me.randomhashtags.worldlaws.service;

import me.randomhashtags.worldlaws.DataValues;
import me.randomhashtags.worldlaws.WLLogger;
import org.json.JSONObject;

import java.time.Clock;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public interface QuotaHandler extends DataValues {
    HashMap<String, Integer> QUOTA_REQUESTS = new HashMap<>();

    default void saveQuota() {
        final JSONObject json = getJSONDataValue(JSONDataValue.QUOTAS);
        for(Map.Entry<String, Integer> map : QUOTA_REQUESTS.entrySet()) {
            final String identifier = map.getKey();
            final int requestsToday = map.getValue();
            json.put(identifier, requestsToday);
        }
        QUOTA_REQUESTS.clear();
        setJSONDataValue(JSONDataValue.QUOTAS, json);
    }
    private boolean canMakeQuotaRequest(String identifier, JSONDataValue value, int amount) {
        final int maxRequestsPerDay = value.getMaxQuotaRequestsPerDay();
        if(QUOTA_REQUESTS.containsKey(identifier)) {
            return QUOTA_REQUESTS.get(identifier) + amount <= maxRequestsPerDay;
        } else {
            final JSONObject json = getJSONDataValue(JSONDataValue.QUOTAS);
            final int requestsToday = json.optInt(identifier, 0);
            QUOTA_REQUESTS.put(identifier, requestsToday);
            return requestsToday+amount <= maxRequestsPerDay;
        }
    }
    default boolean makeQuotaRequest(JSONDataValue value) {
        return makeQuotaRequest(value, 1);
    }
    default boolean makeQuotaRequest(JSONDataValue value, int amount) {
        final String identifier = getIdentifier(value);
        final boolean success = canMakeQuotaRequest(identifier, value, amount);
        if(success) {
            QUOTA_REQUESTS.put(identifier, QUOTA_REQUESTS.get(identifier) + amount);
        } else {
            WLLogger.logError("QuotaHandler", "cannot make \"" + identifier + "\" request due to reaching max allowed quota requests!");
        }
        return success;
    }

    private String getIdentifier(JSONDataValue value) {
        final long epochDay = LocalDate.now(Clock.systemUTC()).toEpochDay();
        return value.getIdentifier() + "-" + epochDay;
    }
}
