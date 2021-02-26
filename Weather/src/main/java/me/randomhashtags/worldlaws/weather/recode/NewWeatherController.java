package me.randomhashtags.worldlaws.weather.recode;

import me.randomhashtags.worldlaws.*;
import me.randomhashtags.worldlaws.location.TerritoryAbbreviations;
import me.randomhashtags.worldlaws.location.WLCountry;
import org.apache.logging.log4j.Level;

import java.util.*;

public interface NewWeatherController extends RestAPI, Jsoupable, TerritoryAbbreviations {

    WLCountry getCountry();
    EventSource getSource();
    String getEvents();

    HashMap<String, String> getEventPreAlerts();
    HashMap<String, String> getTerritoryEvents();
    HashMap<String, HashMap<String, String>> getTerritoryPreAlerts();

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
    default void putEventPreAlerts(HashMap<String, String> eventPreAlerts, HashMap<String, HashSet<NewWeatherPreAlert>> hashmap) {
        for(Map.Entry<String, HashSet<NewWeatherPreAlert>> map : hashmap.entrySet()) {
            final String event = map.getKey();
            final HashSet<NewWeatherPreAlert> preAlerts = map.getValue();
            final StringBuilder builder = new StringBuilder("[");
            boolean isFirst = true;
            for(NewWeatherPreAlert preAlert : preAlerts) {
                builder.append(isFirst ? "" : ",").append(preAlert.toString());
                isFirst = false;
            }
            final String string = builder.append("]").toString();
            eventPreAlerts.put(event.toLowerCase().replace(" ", ""), string);
        }
    }
    default void getPreAlerts(String event, CompletionHandler handler) {
        final HashMap<String, String> eventPreAlerts = getEventPreAlerts();
        if(eventPreAlerts.containsKey(event)) {
            handler.handle(eventPreAlerts.get(event));
        }
    }
    default void putTerritoryEvents(HashMap<String, String> territoryEvents, HashMap<String, HashSet<NewWeatherEvent>> hashmap) {
        boolean isFirst = true;
        for(Map.Entry<String, HashSet<NewWeatherEvent>> map : hashmap.entrySet()) {
            final String territory = map.getKey();
            final HashSet<NewWeatherEvent> events = map.getValue();
            final StringBuilder builder = new StringBuilder("[");
            for(NewWeatherEvent newWeatherEvent : events) {
                builder.append(isFirst ? "" : ",").append(newWeatherEvent.toString());
                isFirst = false;
            }
            builder.append("]");
            territoryEvents.put(territory.toLowerCase().replace(" ", ""), builder.toString());
        }
    }
    default void putTerritoryPreAlerts(HashMap<String, HashMap<String, String>> territoryPreAlerts, HashMap<String, HashMap<String, HashSet<NewWeatherPreAlert>>> hashmap) {
        for(Map.Entry<String, HashMap<String, HashSet<NewWeatherPreAlert>>> map : hashmap.entrySet()) {
            final String territory = map.getKey();
            final HashMap<String, HashSet<NewWeatherPreAlert>> eventsMap = map.getValue();
            final HashMap<String, String> preAlertsMap = new HashMap<>();
            for(Map.Entry<String, HashSet<NewWeatherPreAlert>> eventMap : eventsMap.entrySet()) {
                final String event = eventMap.getKey();
                final HashSet<NewWeatherPreAlert> preAlerts = eventMap.getValue();
                final StringBuilder builder = new StringBuilder("[");
                boolean isFirst = true;
                for(NewWeatherPreAlert preAlert : preAlerts) {
                    builder.append(isFirst ? "" : ",").append(preAlert.toString());
                    isFirst = false;
                }
                final String string = builder.append("]").toString();
                preAlertsMap.put(event, string);
            }
            territoryPreAlerts.put(territory.toLowerCase().replace(" ", ""), preAlertsMap);
        }
    }
    default String getEventsJSON(HashMap<String, Integer> hashmap) {
        final StringBuilder builder = new StringBuilder("[");
        boolean isFirst = true;
        for(Map.Entry<String, Integer> map : hashmap.entrySet()) {
            final String event = map.getKey();
            final Integer value = map.getValue();
            builder.append(isFirst ? "" : ",").append("{\"event\":\"").append(event).append("\",\"defcon\":").append(value).append("}");
            isFirst = false;
        }
        builder.append("]");
        return builder.toString();
    }

    void getAlert(String id, CompletionHandler handler);

    default void getTerritoryEvents(String territory, CompletionHandler handler) {
        final HashMap<String, String> territoryEvents = getTerritoryEvents();
        if(territoryEvents.containsKey(territory)) {
            handler.handle(territoryEvents.get(territory));
        }
    }
    default void getTerritoryPreAlerts(String territory, String event, CompletionHandler handler) {
        final HashMap<String, HashMap<String, String>> territoryPreAlerts = getTerritoryPreAlerts();
        if(territoryPreAlerts.containsKey(territory) && territoryPreAlerts.get(territory).containsKey(event)) {
            handler.handle(territoryPreAlerts.get(territory).get(event));
        }
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
