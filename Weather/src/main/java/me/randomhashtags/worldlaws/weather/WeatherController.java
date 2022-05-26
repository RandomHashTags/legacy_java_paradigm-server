package me.randomhashtags.worldlaws.weather;

import me.randomhashtags.worldlaws.EventSource;
import me.randomhashtags.worldlaws.Jsonable;
import me.randomhashtags.worldlaws.Jsoupable;
import me.randomhashtags.worldlaws.RestAPI;
import me.randomhashtags.worldlaws.country.WLCountry;
import me.randomhashtags.worldlaws.locale.JSONArrayTranslatable;
import me.randomhashtags.worldlaws.locale.JSONObjectTranslatable;
import org.json.JSONArray;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public interface WeatherController extends RestAPI, Jsoupable, Jsonable {
    WLCountry getCountry();
    EventSource getSource();

    HashMap<String, JSONObjectTranslatable> getEventPreAlerts();
    HashMap<String, JSONObjectTranslatable> getSubdivisionEvents();
    HashMap<String, HashMap<String, JSONObjectTranslatable>> getSubdivisionPreAlerts();

    JSONArrayTranslatable getEventTypes();
    JSONObjectTranslatable refresh();

    default void putEventPreAlerts(HashMap<String, JSONObjectTranslatable> eventPreAlerts, ConcurrentHashMap<String, HashSet<WeatherPreAlert>> hashmap) {
        for(Map.Entry<String, HashSet<WeatherPreAlert>> map : hashmap.entrySet()) {
            final String event = map.getKey();
            final HashSet<WeatherPreAlert> preAlerts = map.getValue();

            final HashMap<String, HashSet<WeatherPreAlert>> territoryPreAlerts = new HashMap<>();
            for(WeatherPreAlert preAlert : preAlerts) {
                final HashSet<String> subdivisions = preAlert.getSubdivisions();
                if(subdivisions != null) {
                    for(String subdivision : subdivisions) {
                        territoryPreAlerts.putIfAbsent(subdivision, new HashSet<>());
                        territoryPreAlerts.get(subdivision).add(preAlert);
                    }
                }
            }

            final JSONObjectTranslatable json = new JSONObjectTranslatable();
            for(Map.Entry<String, HashSet<WeatherPreAlert>> preAlert : territoryPreAlerts.entrySet()) {
                final JSONObjectTranslatable subdivisionJSON = new JSONObjectTranslatable();
                final String subdivision = preAlert.getKey();

                for(WeatherPreAlert alert : preAlert.getValue()) {
                    final String id = alert.getID();
                    subdivisionJSON.put(id, alert.toJSONObject(), true);
                }
                json.put(subdivision, subdivisionJSON, true);
            }
            eventPreAlerts.put(event.toLowerCase().replace(" ", ""), json);
        }
    }
    default JSONObjectTranslatable getPreAlerts(String event) {
        final HashMap<String, JSONObjectTranslatable> eventPreAlerts = getEventPreAlerts();
        return eventPreAlerts.get(event);
    }
    default void putSubdivisionEvents(HashMap<String, JSONObjectTranslatable> territoryEvents, ConcurrentHashMap<String, ConcurrentHashMap<String, WeatherEvent>> hashmap) {
        for(Map.Entry<String, ConcurrentHashMap<String, WeatherEvent>> map : hashmap.entrySet()) {
            final String territory = map.getKey();
            final ConcurrentHashMap<String, WeatherEvent> events = map.getValue();

            final HashMap<Integer, HashSet<String>> defconMap = new HashMap<>();
            for(WeatherEvent weatherEvent : events.values()) {
                final int defcon = weatherEvent.getDefcon();
                defconMap.putIfAbsent(defcon, new HashSet<>());
                defconMap.get(defcon).add(weatherEvent.getEvent());
            }

            final JSONObjectTranslatable json = getDefconJSON(defconMap);
            territoryEvents.put(territory.toLowerCase().replace(" ", ""), json);
        }
    }
    default void putSubdivisionPreAlerts(HashMap<String, HashMap<String, JSONObjectTranslatable>> territoryPreAlerts, ConcurrentHashMap<String, ConcurrentHashMap<String, HashSet<WeatherPreAlert>>> hashmap) {
        for(Map.Entry<String, ConcurrentHashMap<String, HashSet<WeatherPreAlert>>> map : hashmap.entrySet()) {
            final String territory = map.getKey();
            final ConcurrentHashMap<String, HashSet<WeatherPreAlert>> eventsMap = map.getValue();
            final HashMap<String, JSONObjectTranslatable> preAlertsMap = new HashMap<>();
            for(Map.Entry<String, HashSet<WeatherPreAlert>> eventMap : eventsMap.entrySet()) {
                final String event = eventMap.getKey();
                final HashSet<WeatherPreAlert> preAlerts = eventMap.getValue();
                final JSONObjectTranslatable json = new JSONObjectTranslatable();
                for(WeatherPreAlert preAlert : preAlerts) {
                    final String id = preAlert.getID();
                    json.put(id, preAlert.toJSONObject(), true);
                }
                preAlertsMap.put(event, json);
            }
            territoryPreAlerts.put(territory.toLowerCase().replace(" ", ""), preAlertsMap);
        }
    }
    default JSONObjectTranslatable getEventsJSON(ConcurrentHashMap<String, Integer> hashmap) {
        final HashMap<Integer, HashSet<String>> defcons = new HashMap<>();
        for(Map.Entry<String, Integer> map : hashmap.entrySet()) {
            final String event = map.getKey();
            final int defcon = map.getValue();
            if(!defcons.containsKey(defcon)) {
                defcons.put(defcon, new HashSet<>());
            }
            defcons.get(defcon).add(event);
        }
        return getDefconJSON(defcons);
    }
    private JSONObjectTranslatable getDefconJSON(HashMap<Integer, HashSet<String>> defcons) {
        final JSONObjectTranslatable json = new JSONObjectTranslatable();
        for(Map.Entry<Integer, HashSet<String>> map : defcons.entrySet()) {
            final String defcon = Integer.toString(map.getKey());
            final JSONArray array = new JSONArray(map.getValue());
            json.put(defcon, array, true);
        }
        return json;
    }

    JSONObjectTranslatable getAlert(String id);

    default JSONObjectTranslatable getSubdivisionEvents(String subdivision) {
        final HashMap<String, JSONObjectTranslatable> territoryEvents = getSubdivisionEvents();
        return territoryEvents.get(subdivision);
    }
    default JSONObjectTranslatable getSubdivisionPreAlerts(String subdivision, String event) {
        final HashMap<String, HashMap<String, JSONObjectTranslatable>> territoryPreAlerts = getSubdivisionPreAlerts();
        JSONObjectTranslatable string = null;
        if(territoryPreAlerts.containsKey(subdivision) && territoryPreAlerts.get(subdivision).containsKey(event)) {
            string = territoryPreAlerts.get(subdivision).get(event);
        }
        return string;
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
