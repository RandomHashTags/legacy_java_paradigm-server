package me.randomhashtags.worldlaws.weather.recode;

import me.randomhashtags.worldlaws.*;
import me.randomhashtags.worldlaws.location.WLCountry;

import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;

public interface NewWeatherController extends RestAPI, Jsoupable {

    WLCountry getCountry();
    EventSource getSource();
    String getEvents();

    void refresh(CompletionHandler handler);

    default void startAutoUpdates(long interval, CompletionHandler handler, CompletionHandler autoUpdateHandler) {
        final String id = getCountry().getBackendID(), className = getClass().getSimpleName();
        new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                refresh(getTest(className, id, System.currentTimeMillis(), autoUpdateHandler));
            }
        }, interval, interval);
        refresh(getTest(className, id, System.currentTimeMillis(), handler));
    }

    default void getEvents(CompletionHandler handler) {
        final String events = getEvents();
        if(events != null) {
            handler.handle(events);
        } else {
            refresh(new CompletionHandler() {
                @Override
                public void handle(Object object) {
                    handler.handle(getEvents());
                }
            });
        }
    }
    void getPreAlerts(String event, CompletionHandler handler);
    void getAlert(String id, CompletionHandler handler);

    void getTerritoryEvents(String territory, CompletionHandler handler);
    void getTerritoryPreAlerts(String territory, String event, CompletionHandler handler);

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
