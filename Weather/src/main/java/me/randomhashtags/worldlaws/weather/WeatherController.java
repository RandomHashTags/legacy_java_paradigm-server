package me.randomhashtags.worldlaws.weather;

import me.randomhashtags.worldlaws.*;
import me.randomhashtags.worldlaws.country.WLCountry;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public interface WeatherController extends RestAPI, Jsoupable, Jsonable {
    WLCountry getCountry();
    EventSource getSource();
    String getEvents();

    HashMap<String, String> getEventPreAlerts();
    HashMap<String, String> getSubdivisionEvents();
    HashMap<String, HashMap<String, String>> getSubdivisionPreAlerts();

    void refresh(CompletionHandler handler);

    default void startAutoUpdates(CompletionHandler handler, CompletionHandler autoUpdateHandler) {
        Weather.INSTANCE.registerFixedTimer(WLUtilities.WEATHER_ALERTS_UPDATE_INTERVAL, new CompletionHandler() {
            @Override
            public void handleObject(Object object) {
                refresh(autoUpdateHandler);
            }
        });
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
                final String subdivision = preAlert.getSubdivision();
                territoryPreAlerts.putIfAbsent(subdivision, new HashSet<>());
                territoryPreAlerts.get(subdivision).add(preAlert.toString());
            }

            final StringBuilder builder = new StringBuilder("{");
            boolean isFirstTerritory = true;
            for(Map.Entry<String, HashSet<String>> preAlert : territoryPreAlerts.entrySet()) {
                final String subdivision = preAlert.getKey();
                builder.append(isFirstTerritory ? "" : ",").append("\"").append(subdivision).append("\":{");
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
        handler.handleString(eventPreAlerts.getOrDefault(event, null));
    }
    default void putSubdivisionEvents(HashMap<String, String> territoryEvents, ConcurrentHashMap<String, HashSet<WeatherEvent>> hashmap) {
        for(Map.Entry<String, HashSet<WeatherEvent>> map : hashmap.entrySet()) {
            final String territory = map.getKey();
            final HashSet<WeatherEvent> events = map.getValue();

            final HashMap<Integer, HashSet<String>> defconMap = new HashMap<>();
            for(WeatherEvent weatherEvent : events) {
                final int defcon = weatherEvent.getDefcon();
                defconMap.putIfAbsent(defcon, new HashSet<>());
                defconMap.get(defcon).add(weatherEvent.getEvent());
            }

            final StringBuilder builder = new StringBuilder("{");
            boolean isFirstDefcon = true;
            for(Map.Entry<Integer, HashSet<String>> entry : defconMap.entrySet()) {
                final int defcon = entry.getKey();
                builder.append(isFirstDefcon ? "" : ",").append("\"").append(defcon).append("\":[");
                boolean isFirstEvent = true;
                for(String string : entry.getValue()) {
                    builder.append(isFirstEvent ? "" : ",").append("\"").append(string).append("\"");
                    isFirstEvent = false;
                }
                builder.append("]");
                isFirstDefcon = false;
            }
            builder.append("}");
            territoryEvents.put(territory.toLowerCase().replace(" ", ""), builder.toString());
        }
    }
    default void putSubdivisionPreAlerts(HashMap<String, HashMap<String, String>> territoryPreAlerts, ConcurrentHashMap<String, ConcurrentHashMap<String, HashSet<WeatherPreAlert>>> hashmap) {
        for(Map.Entry<String, ConcurrentHashMap<String, HashSet<WeatherPreAlert>>> map : hashmap.entrySet()) {
            final String territory = map.getKey();
            final ConcurrentHashMap<String, HashSet<WeatherPreAlert>> eventsMap = map.getValue();
            final HashMap<String, String> preAlertsMap = new HashMap<>();
            for(Map.Entry<String, HashSet<WeatherPreAlert>> eventMap : eventsMap.entrySet()) {
                final String event = eventMap.getKey();
                final HashSet<WeatherPreAlert> preAlerts = eventMap.getValue();
                final StringBuilder builder = new StringBuilder("{");
                boolean isFirst = true;
                for(WeatherPreAlert preAlert : preAlerts) {
                    builder.append(isFirst ? "" : ",").append(preAlert.toString());
                    isFirst = false;
                }
                final String string = builder.append("}").toString();
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

    default void getSubdivisionEvents(String subdivision, CompletionHandler handler) {
        final HashMap<String, String> territoryEvents = getSubdivisionEvents();
        final String value = territoryEvents.getOrDefault(subdivision, null);
        handler.handleString(value);
    }
    default void getSubdivisionPreAlerts(String subdivision, String event, CompletionHandler handler) {
        final HashMap<String, HashMap<String, String>> territoryPreAlerts = getSubdivisionPreAlerts();
        if(territoryPreAlerts.containsKey(subdivision) && territoryPreAlerts.get(subdivision).containsKey(event)) {
            handler.handleString(territoryPreAlerts.get(subdivision).get(event));
        } else {
            handler.handleString(null);
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
