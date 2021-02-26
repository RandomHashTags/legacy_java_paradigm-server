package me.randomhashtags.worldlaws.weather.recode.country;

import me.randomhashtags.worldlaws.CompletionHandler;
import me.randomhashtags.worldlaws.EventSource;
import me.randomhashtags.worldlaws.RequestMethod;
import me.randomhashtags.worldlaws.WLLogger;
import me.randomhashtags.worldlaws.location.WLCountry;
import me.randomhashtags.worldlaws.location.Location;
import me.randomhashtags.worldlaws.weather.WeatherAlertTime;
import me.randomhashtags.worldlaws.weather.recode.*;
import org.apache.logging.log4j.Level;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public enum WeatherUSA implements NewWeatherController {
    INSTANCE;

    private String eventsJSON;
    private HashMap<String, String> alertIDs, zones, eventPreAlerts, territoryEvents;
    private HashMap<String, HashMap<String, String>> territoryPreAlerts;
    private HashMap<String, NewWeatherPreAlert> preAlertIDs;

    @Override
    public WLCountry getCountry() {
        return WLCountry.UNITED_STATES;
    }

    @Override
    public EventSource getSource() {
        return new EventSource("U.S. National Weather Service", "https://www.weather.gov");
    }

    @Override
    public String getEvents() {
        return eventsJSON;
    }

    @Override
    public HashMap<String, String> getEventPreAlerts() {
        return eventPreAlerts;
    }

    @Override
    public HashMap<String, String> getTerritoryEvents() {
        return territoryEvents;
    }

    @Override
    public HashMap<String, HashMap<String, String>> getTerritoryPreAlerts() {
        return territoryPreAlerts;
    }

    @Override
    public void refresh(CompletionHandler handler) {
        eventsJSON = "[]";
        alertIDs = new HashMap<>();
        eventPreAlerts = new HashMap<>();
        territoryEvents = new HashMap<>();
        territoryPreAlerts = new HashMap<>();
        preAlertIDs = new HashMap<>();
        final String country = getCountry().getBackendID(), url = "https://api.weather.gov/alerts/active?status=actual";

        final HashMap<String, Integer> eventsMap = new HashMap<>();
        final HashMap<String, HashSet<NewWeatherPreAlert>> eventPreAlertsMap = new HashMap<>();
        final HashMap<String, HashSet<NewWeatherEvent>> territoryEventsMap = new HashMap<>();
        final HashMap<String, HashMap<String, HashSet<NewWeatherPreAlert>>> territoryPreAlertsMap = new HashMap<>();

        final HashMap<String, String> headers = new HashMap<>(CONTENT_HEADERS);
        headers.put("User-Agent", "(World Laws.Weather, ***REMOVED***)");
        final String zonePrefix = "https://api\\.weather\\.gov/zones/";
        requestJSONObject(url, RequestMethod.GET, headers, new CompletionHandler() {
            @Override
            public void handleJSONObject(JSONObject json) {
                if(json != null) {
                    final HashMap<String, String> territories = getAmericanTerritories();
                    final JSONArray array = json.getJSONArray("features");
                    for(Object obj : array) {
                        final JSONObject jsonAlert = (JSONObject) obj;
                        final String id = jsonAlert.getString("id").split("/alerts/")[1];
                        final JSONObject properties = jsonAlert.getJSONObject("properties");
                        final JSONArray affectedZones = properties.getJSONArray("affectedZones");
                        final HashSet<String> zoneIDs = new HashSet<>();
                        for(Object object : affectedZones) {
                            final String zoneURL = (String) object;
                            final String zoneID = zoneURL.split(zonePrefix)[1];
                            zoneIDs.add(zoneID);
                        }

                        final String[] senderName = properties.getString("senderName").split(" ");
                        final int senderNameLength = senderName.length;
                        final String territoryAbbreviation = senderName[senderNameLength-1];
                        final String territory = territories.getOrDefault(territoryAbbreviation, "Unknown");
                        final String severityString = properties.getString("severity"), severity = severityString.equals("Unknown") ? "-1" : severityString;
                        final String certainty = properties.getString("certainty");
                        final String event = properties.getString("event");
                        final String headline = properties.has("headline") ? properties.getString("headline") : null;
                        final String description = properties.getString("description")
                                .replace("\n\n", "%double_bruh%")
                                .replace(".\n", "%bruh%")
                                .replace("\n", " ")
                                .replace("%bruh%", ".\n")
                                .replace("%double_bruh%", "\n\n");
                        final String instruction = properties.has("instruction") && properties.get("instruction") instanceof String
                                ? properties.getString("instruction")
                                    .replace(".\n", "%bruh%")
                                    .replace("\n", " ")
                                    .replace("%bruh%", ".\n")
                                : null;
                        final String sent = properties.getString("sent");
                        final String effective = properties.getString("effective");
                        final String expires = properties.getString("expires");
                        final String ends = properties.get("ends") instanceof String ? properties.getString("ends") : null;
                        final WeatherAlertTime time = new WeatherAlertTime(sent, effective, expires, ends);

                        final int defcon = getSeverityDEFCON(severity);
                        if(!eventsMap.containsKey(event)) {
                            eventsMap.put(event, defcon);
                        }

                        if(!territoryEventsMap.containsKey(territory)) {
                            territoryEventsMap.put(territory, new HashSet<>());
                        }
                        final HashSet<NewWeatherEvent> territorySet = territoryEventsMap.get(territory);
                        boolean hasEvent = false;
                        for(NewWeatherEvent newWeatherEvent : territorySet) {
                            if(event.equals(newWeatherEvent.getEvent())) {
                                hasEvent = true;
                                break;
                            }
                        }
                        if(!hasEvent) {
                            final NewWeatherEvent weatherEvent = new NewWeatherEvent(event, defcon);
                            territoryEventsMap.get(territory).add(weatherEvent);
                        }

                        /*final String areaDesc = properties.getString("areaDesc");
                        final HashMap<String, HashSet<String>> areas = new HashMap<>();
                        if(areaDesc.contains(";")) {
                            final HashSet<String> territoryAreas = new HashSet<>();
                            for(String value : areaDesc.split(";")) {
                                final String[] values = value.split(" ");
                                final String lastValue = values[values.length-1];
                                final String targetTerritory = territories.getOrDefault(lastValue, null);
                                if(targetTerritory != null) {
                                    value = value.substring(0, value.length()-3);
                                    if(!areas.containsKey(targetTerritory)) {
                                        areas.put(targetTerritory, new HashSet<>());
                                    }
                                    areas.get(targetTerritory).add(value);
                                } else {
                                    territoryAreas.add(value);
                                }
                            }
                            areas.put(territory, territoryAreas);
                        }*/

                        final NewWeatherPreAlert preAlert = new NewWeatherPreAlert(event, id, country, territory, severity, certainty, headline, instruction, description, zoneIDs, time);
                        preAlertIDs.put(id, preAlert);

                        if(!eventPreAlertsMap.containsKey(event)) {
                            eventPreAlertsMap.put(event, new HashSet<>());
                        }
                        eventPreAlertsMap.get(event).add(preAlert);

                        if(!territoryPreAlertsMap.containsKey(territory)) {
                            territoryPreAlertsMap.put(territory, new HashMap<>());
                        }
                        if(!territoryPreAlertsMap.get(territory).containsKey(event)) {
                            territoryPreAlertsMap.get(territory).put(event, new HashSet<>());
                        }
                        territoryPreAlertsMap.get(territory).get(event).add(preAlert);
                    }
                }

                putEventPreAlerts(eventPreAlerts, eventPreAlertsMap);
                putTerritoryEvents(territoryEvents, territoryEventsMap);
                putTerritoryPreAlerts(territoryPreAlerts, territoryPreAlertsMap);
                eventsJSON = getEventsJSON(eventsMap);

                if(handler != null) {
                    handler.handle(eventsJSON);
                }
            }
        });
    }

    @Override
    public void getAlert(String id, CompletionHandler handler) {
        if(alertIDs == null) {
            refresh(new CompletionHandler() {
                @Override
                public void handle(Object object) {
                    tryGettingAlert(id, handler);
                }
            });
        } else {
            tryGettingAlert(id, handler);
        }
    }
    private void tryGettingAlert(String id, CompletionHandler handler) {
        if(alertIDs.containsKey(id)) {
            handler.handle(alertIDs.get(id));
        } else if(preAlertIDs.containsKey(id)) {
            final EventSource source = getSource();
            final NewWeatherPreAlert preAlert = preAlertIDs.get(id);
            final HashSet<String> zones = preAlert.getZoneIDs();
            final int max = zones.size();
            final AtomicInteger completed = new AtomicInteger(0);
            final StringBuilder builder = new StringBuilder("[");
            zones.parallelStream().forEach(zoneID -> getZone(zoneID, new CompletionHandler() {
                @Override
                public void handle(Object object) {
                    builder.append(completed.get() == 0 ? "" : ",").append(object);
                    final int value = completed.addAndGet(1);
                    if(value == max) {
                        builder.append("]");
                        final String zonesJSON = builder.toString();
                        final NewWeatherAlert alert = new NewWeatherAlert(preAlert, zonesJSON, source);
                        final String string = alert.toString();
                        alertIDs.put(id, string);
                        preAlertIDs.remove(id);
                        handler.handle(string);
                    }
                }
            }));
        }
    }

    private void getZone(String zoneID, CompletionHandler handler) {
        if(zones == null) {
            zones = new HashMap<>();
        }
        if(zones.containsKey(zoneID)) {
            handler.handle(zones.get(zoneID));
        } else {
            final String url = "https://api.weather.gov/zones/" + zoneID;
            requestJSONObject(url, RequestMethod.GET, new CompletionHandler() {
                @Override
                public void handleJSONObject(JSONObject object) {
                    final HashMap<String, String> territories = getAmericanTerritories();
                    final JSONObject geometryJSON = object.getJSONObject("geometry"), properties = object.getJSONObject("properties");
                    final Object state = properties.get("state");
                    final String name = properties.getString("name"), territory = state instanceof String ? territories.getOrDefault(state, "Unknown") : "Unknown";
                    final List<Location> geometry = getGeometry(geometryJSON);
                    final WeatherZone zone = new WeatherZone(zoneID, name, territory, geometry);
                    final String string = zone.toString();
                    zones.put(zoneID, string);
                    handler.handle(string);
                }
            });
        }
    }
    private List<Location> getGeometry(JSONObject geometryJSON) {
        final String geometryType = geometryJSON.getString("type");
        final List<Location> geometry = new ArrayList<>();
        switch (geometryType) {
            case "MultiPolygon":
                final JSONArray coordinates = geometryJSON.getJSONArray("coordinates").getJSONArray(0).getJSONArray(0);

                for(Object coordinate : coordinates) {
                    final JSONArray array = (JSONArray) coordinate;
                    final double longitude = array.getDouble(0), latitude = array.getDouble(1);
                    final Location location = new Location(latitude, longitude);
                    geometry.add(location);
                }
                break;
            case "GeometryCollection":
                final JSONArray array = geometryJSON.getJSONArray("geometries");
                for(Object obj : array) {
                    final JSONObject json = (JSONObject) obj;
                    final List<Location> targetLocations = getGeometry(json);
                    geometry.addAll(targetLocations);
                }
                break;
            default:
                WLLogger.log(Level.WARN, "WeatherUSA - uncaught geometryType \"" + geometryType + "\"!");
                break;
        }
        return geometry;
    }
}
