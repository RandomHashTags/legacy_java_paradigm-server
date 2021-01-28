package me.randomhashtags.worldlaws.weather;

import me.randomhashtags.worldlaws.*;
import me.randomhashtags.worldlaws.location.WLCountry;

import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;

public interface WeatherController extends RestAPI, Jsoupable {
    WLCountry getCountryBackendID();
    EventSource getSource();
    String getAlertEvents();
    default void getAlertEvents(CompletionHandler handler) {
        final String alertEvents = getAlertEvents();
        if(alertEvents != null) {
            handler.handle(alertEvents);
        } else {
            refreshAlerts(handler);
        }
    }
    HashMap<String, String> getEventAlerts();
    HashMap<String, HashMap<String, String>> getTerritoryEventAlerts();
    HashMap<String, String> getTerritoryAlerts();

    default void startAutoUpdates(long interval, CompletionHandler handler, CompletionHandler autoUpdateHandler) {
        final String id = getCountryBackendID().getBackendID(), className = getClass().getSimpleName();
        new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                refreshAlerts(getTest(className, id, System.currentTimeMillis(), autoUpdateHandler));
            }
        }, interval, interval);
        refreshAlerts(getTest(className, id, System.currentTimeMillis(), handler));
    }
    void refreshAlerts(CompletionHandler handler);

    default void getEventAlerts(String event, CompletionHandler handler) {
        final HashMap<String, String> eventAlerts = getEventAlerts();
        final String value = eventAlerts != null ? eventAlerts.getOrDefault(event, "[]") : "[]";
        handler.handle(value);
    }
    default void getTerritoryEventAlerts(String territory, String event, CompletionHandler handler) {
        final HashMap<String, HashMap<String, String>> territoryEventAlerts = getTerritoryEventAlerts();
        final String value = territoryEventAlerts != null && territoryEventAlerts.containsKey(territory) ? territoryEventAlerts.get(territory).getOrDefault(event, "[]") : "[]";
        handler.handle(value);
    }
    default void getTerritoryAlerts(String territory, CompletionHandler handler) {
        final HashMap<String, String> territoryAlerts = getTerritoryAlerts();
        final String value = territoryAlerts != null ? territoryAlerts.getOrDefault(territory, "[]") : "[]";
        handler.handle(value);
    }

    default int getSeverityDEFCON(String severity) {
        switch (severity.toLowerCase()) {
            case "extreme": return 1;
            case "severe": return 2;
            case "moderate": return 3;
            case "minor": return 4;
            default: return 5;
        }
    }

    private static CompletionHandler getTest(String className, String id, long started, CompletionHandler handler) {
        return new CompletionHandler() {
            @Override
            public void handle(Object object) {
                WLLogger.log(Level.INFO, className +  " - refreshed alerts for country \"" + id + "\" (took " + (System.currentTimeMillis()-started) + "ms)");
                if(handler != null) {
                    handler.handle(object);
                }
            }
        };
    }
}
