package me.randomhashtags.worldlaws.weather;

import me.randomhashtags.worldlaws.*;
import me.randomhashtags.worldlaws.location.WLCountry;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public interface WeatherController extends RestAPI, Jsoupable, Jsonable {
    WLCountry getCountry();
    EventSource getSource();
    String getEvents();

    HashMap<String, String> getEventPreAlerts();
    HashMap<String, String> getTerritoryEvents();
    HashMap<String, HashMap<String, String>> getTerritoryPreAlerts();

    void refresh(CompletionHandler handler);

    default void startAutoUpdates(CompletionHandler handler, CompletionHandler autoUpdateHandler) {
        final long interval = WLUtilities.WEATHER_ALERTS_UPDATE_INTERVAL;
        new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                refresh(autoUpdateHandler);
            }
        }, interval, interval);
        refresh(handler);
    }

    default void getEvents(CompletionHandler handler) {
        final String events = getEvents();
        if(events != null) {
            handler.handleString(events);
        } else {
            refresh(new CompletionHandler() {
                @Override
                public void handleString(String string) {
                    handler.handleString(getEvents());
                }
            });
        }
    }
    default void putEventPreAlerts(HashMap<String, String> eventPreAlerts, ConcurrentHashMap<String, HashSet<WeatherPreAlert>> hashmap) {
        for(Map.Entry<String, HashSet<WeatherPreAlert>> map : hashmap.entrySet()) {
            final String event = map.getKey();
            final HashSet<WeatherPreAlert> preAlerts = map.getValue();

            final HashMap<String, HashSet<String>> territoryPreAlerts = new HashMap<>();
            for(WeatherPreAlert preAlert : preAlerts) {
                final String territory = preAlert.getTerritory();
                territoryPreAlerts.putIfAbsent(territory, new HashSet<>());
                territoryPreAlerts.get(territory).add(preAlert.toString());
            }

            final StringBuilder builder = new StringBuilder("{");
            boolean isFirstTerritory = true;
            for(Map.Entry<String, HashSet<String>> preAlert : territoryPreAlerts.entrySet()) {
                final String territory = preAlert.getKey();
                builder.append(isFirstTerritory ? "" : ",").append("\"").append(territory).append("\":{");
                isFirstTerritory = false;

                boolean isFirst = true;
                for(String alert : preAlert.getValue()) {
                    builder.append(isFirst ? "" : ",").append(alert);
                    isFirst = false;
                }
                builder.append("}");
            }
            final String string = builder.append("}").toString();
            eventPreAlerts.put(event.toLowerCase().replace(" ", ""), string);
        }
    }
    default void getPreAlerts(String event, CompletionHandler handler) {
        final HashMap<String, String> eventPreAlerts = getEventPreAlerts();
        if(eventPreAlerts.containsKey(event)) {
            handler.handleString(eventPreAlerts.get(event));
        }
    }
    default void putTerritoryEvents(HashMap<String, String> territoryEvents, ConcurrentHashMap<String, HashSet<WeatherEvent>> hashmap) {
        boolean isFirst = true;
        for(Map.Entry<String, HashSet<WeatherEvent>> map : hashmap.entrySet()) {
            final String territory = map.getKey();
            final HashSet<WeatherEvent> events = map.getValue();
            final StringBuilder builder = new StringBuilder("[");
            for(WeatherEvent newWeatherEvent : events) {
                builder.append(isFirst ? "" : ",").append(newWeatherEvent.toString());
                isFirst = false;
            }
            builder.append("]");
            territoryEvents.put(territory.toLowerCase().replace(" ", ""), builder.toString());
        }
    }
    default void putTerritoryPreAlerts(HashMap<String, HashMap<String, String>> territoryPreAlerts, ConcurrentHashMap<String, ConcurrentHashMap<String, HashSet<WeatherPreAlert>>> hashmap) {
        for(Map.Entry<String, ConcurrentHashMap<String, HashSet<WeatherPreAlert>>> map : hashmap.entrySet()) {
            final String territory = map.getKey();
            final ConcurrentHashMap<String, HashSet<WeatherPreAlert>> eventsMap = map.getValue();
            final HashMap<String, String> preAlertsMap = new HashMap<>();
            for(Map.Entry<String, HashSet<WeatherPreAlert>> eventMap : eventsMap.entrySet()) {
                final String event = eventMap.getKey();
                final HashSet<WeatherPreAlert> preAlerts = eventMap.getValue();
                final StringBuilder builder = new StringBuilder("[");
                boolean isFirst = true;
                for(WeatherPreAlert preAlert : preAlerts) {
                    builder.append(isFirst ? "" : ",").append(preAlert.toString());
                    isFirst = false;
                }
                final String string = builder.append("]").toString();
                preAlertsMap.put(event, string);
            }
            territoryPreAlerts.put(territory.toLowerCase().replace(" ", ""), preAlertsMap);
        }
    }
    default String getEventsJSON(ConcurrentHashMap<String, Integer> hashmap) {
        final StringBuilder builder = new StringBuilder("{");
        final HashMap<Integer, HashSet<String>> defcons = new HashMap<>();
        for(Map.Entry<String, Integer> map : hashmap.entrySet()) {
            final String event = map.getKey();
            final int defcon = map.getValue();
            if(!defcons.containsKey(defcon)) {
                defcons.put(defcon, new HashSet<>());
            }
            defcons.get(defcon).add(event);
        }

        boolean isFirstDefcon = true;
        for(Map.Entry<Integer, HashSet<String>> map : defcons.entrySet()) {
            final int defcon = map.getKey();
            final HashSet<String> events = map.getValue();
            final StringBuilder eventBuilder = new StringBuilder("[");
            boolean isFirstEvent = true;
            for(String event : events) {
                eventBuilder.append(isFirstEvent ? "" : ",").append("\"").append(event).append("\"");
                isFirstEvent = false;
            }
            eventBuilder.append("]");
            builder.append(isFirstDefcon ? "" : ",").append("\"").append(defcon).append("\":").append(eventBuilder.toString());
            isFirstDefcon = false;
        }
        builder.append("}");
        return builder.toString();
    }

    void getZones(String[] zones, CompletionHandler handler);
    void getZone(String zoneID, CompletionHandler handler);
    void getAlert(String id, CompletionHandler handler);

    default void getTerritoryEvents(String territory, CompletionHandler handler) {
        final HashMap<String, String> territoryEvents = getTerritoryEvents();
        if(territoryEvents.containsKey(territory)) {
            handler.handleString(territoryEvents.get(territory));
        }
    }
    default void getTerritoryPreAlerts(String territory, String event, CompletionHandler handler) {
        final HashMap<String, HashMap<String, String>> territoryPreAlerts = getTerritoryPreAlerts();
        if(territoryPreAlerts.containsKey(territory) && territoryPreAlerts.get(territory).containsKey(event)) {
            handler.handleString(territoryPreAlerts.get(territory).get(event));
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
}
