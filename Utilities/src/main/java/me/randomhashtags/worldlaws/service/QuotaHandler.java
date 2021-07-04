package me.randomhashtags.worldlaws.service;

import me.randomhashtags.worldlaws.CompletionHandler;
import me.randomhashtags.worldlaws.DataValues;
import me.randomhashtags.worldlaws.WLLogger;
import org.apache.logging.log4j.Level;
import org.json.JSONObject;

import java.time.Clock;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public interface QuotaHandler extends DataValues {
    HashMap<String, Integer> QUOTA_REQUESTS = new HashMap<>();

    default void saveQuota() {
        getJSONDataValue(JSONDataValue.QUOTAS, new CompletionHandler() {
            @Override
            public void handleJSONObject(JSONObject json) {
                for(Map.Entry<String, Integer> map : QUOTA_REQUESTS.entrySet()) {
                    final String identifier = map.getKey();
                    final int requestsToday = map.getValue();
                    json.put(identifier, requestsToday);
                }
                QUOTA_REQUESTS.clear();
                setJSONDataValue(JSONDataValue.QUOTAS, json);
            }
        });
    }
    private void canMakeQuotaRequest(String identifier, JSONDataValue value, CompletionHandler handler) {
        final int maxRequestsPerDay = value.getMaxQuotaRequestsPerDay();
        if(QUOTA_REQUESTS.containsKey(identifier)) {
            handler.handleBoolean(QUOTA_REQUESTS.get(identifier) < maxRequestsPerDay);
        } else {
            getJSONDataValue(JSONDataValue.QUOTAS, new CompletionHandler() {
                @Override
                public void handleJSONObject(JSONObject json) {
                    final int requestsToday = json.has(identifier) ? json.getInt(identifier) : 0;
                    QUOTA_REQUESTS.put(identifier, requestsToday);
                    handler.handleBoolean(requestsToday < maxRequestsPerDay);
                }
            });
        }
    }
    default void makeQuotaRequest(JSONDataValue value, CompletionHandler handler) {
        final long epochDay = LocalDate.now(Clock.systemUTC()).toEpochDay();
        final String identifier = value.getIdentifier() + "-" + epochDay;
        canMakeQuotaRequest(identifier, value, new CompletionHandler() {
            @Override
            public void handleBoolean(boolean success) {
                if(success) {
                    QUOTA_REQUESTS.put(identifier, QUOTA_REQUESTS.get(identifier) + 1);
                    handler.handleObject(null);
                } else {
                    WLLogger.log(Level.WARN, "QuotaHandler - cannot make \"" + identifier + "\" request due to reaching max allowed quota requests!");
                }
            }
        });
    }
}
