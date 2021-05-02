package me.randomhashtags.worldlaws.weather.country;

import me.randomhashtags.worldlaws.*;
import me.randomhashtags.worldlaws.location.TerritoryAbbreviations;
import me.randomhashtags.worldlaws.location.WLCountry;
import me.randomhashtags.worldlaws.location.Location;
import me.randomhashtags.worldlaws.weather.*;
import org.apache.logging.log4j.Level;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public enum WeatherUSA implements WeatherController {
    INSTANCE;

    private String eventsJSON;
    private HashMap<String, String> alertIDs, zones, eventPreAlerts, territoryEvents;
    private HashMap<String, HashMap<String, String>> territoryPreAlerts;
    private HashMap<String, WeatherPreAlert> preAlertIDs;

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
        eventsJSON = "{}";
        alertIDs = new HashMap<>();
        eventPreAlerts = new HashMap<>();
        territoryEvents = new HashMap<>();
        territoryPreAlerts = new HashMap<>();
        preAlertIDs = new HashMap<>();
        final String url = "https://api.weather.gov/alerts/active?status=actual";

        final HashMap<String, Integer> eventsMap = new HashMap<>();
        final HashMap<String, HashSet<WeatherPreAlert>> eventPreAlertsMap = new HashMap<>();
        final HashMap<String, HashSet<WeatherEvent>> territoryEventsMap = new HashMap<>();
        final HashMap<String, HashMap<String, HashSet<WeatherPreAlert>>> territoryPreAlertsMap = new HashMap<>();

        final HashMap<String, String> headers = new HashMap<>(CONTENT_HEADERS);
        headers.put("User-Agent", "(World Laws.Weather, ***REMOVED***)");
        final String zonePrefix = "https://api\\.weather\\.gov/zones/";
        requestJSONObject(url, RequestMethod.GET, headers, new CompletionHandler() {
            @Override
            public void handleJSONObject(JSONObject json) {
                if(json != null) {
                    final HashMap<String, String> territories = TerritoryAbbreviations.getAmericanTerritories();
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
                        final HashSet<WeatherEvent> territorySet = territoryEventsMap.get(territory);
                        boolean hasEvent = false;
                        for(WeatherEvent newWeatherEvent : territorySet) {
                            if(event.equals(newWeatherEvent.getEvent())) {
                                hasEvent = true;
                                break;
                            }
                        }
                        if(!hasEvent) {
                            final WeatherEvent weatherEvent = new WeatherEvent(event, defcon);
                            territoryEventsMap.get(territory).add(weatherEvent);
                        }

                        final WeatherPreAlert preAlert = new WeatherPreAlert(defcon, event, id, territory, certainty, headline, instruction, description, zoneIDs, time);
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
            final WeatherPreAlert preAlert = preAlertIDs.get(id);
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
                        final WeatherAlert alert = new WeatherAlert(preAlert, zonesJSON, source);
                        final String string = alert.toString();
                        alertIDs.put(id, string);
                        preAlertIDs.remove(id);
                        handler.handle(string);
                    }
                }
            }));
        }
    }

    @Override
    public void getZones(String[] zones, CompletionHandler handler) {
        final int max = zones.length;
        final HashSet<String> zoneJSONs = new HashSet<>();
        final AtomicInteger completed = new AtomicInteger(0);
        Arrays.stream(zones).parallel().forEach(zoneID -> {
            getZone(zoneID, new CompletionHandler() {
                @Override
                public void handle(Object object) {
                    final int value = completed.addAndGet(1);
                    zoneJSONs.add(object.toString());
                    if(value == max) {
                        final StringBuilder builder = new StringBuilder("[");
                        boolean isFirst = true;
                        for(String zoneJSON : zoneJSONs) {
                            builder.append(isFirst ? "" : ",").append(zoneJSON);
                            isFirst = false;
                        }
                        builder.append("]");
                        handler.handle(builder.toString());
                    }
                }
            });
        });
    }
    @Override
    public void getZone(String zoneID, CompletionHandler handler) {
        if(zones == null) {
            zones = new HashMap<>();
        }
        if(zones.containsKey(zoneID)) {
            handler.handle(zones.get(zoneID));
        } else {
            final String[] values = zoneID.split("/");
            final String zoneFolder = values[0], zone = values[1];
            getJSONObject(FileType.WEATHER_USA_ZONES, zoneFolder + File.separator + zone, new CompletionHandler() {
                @Override
                public void load(CompletionHandler handler) {
                    final String url = "https://api.weather.gov/zones/" + zoneID;
                    requestJSONObject(url, RequestMethod.GET, new CompletionHandler() {
                        @Override
                        public void handleJSONObject(JSONObject object) {
                            handler.handle(object.toString());
                        }
                    });
                }

                @Override
                public void handleJSONObject(JSONObject json) {
                    final HashMap<String, String> territories = TerritoryAbbreviations.getAmericanTerritories();
                    final JSONObject geometryJSON = json.getJSONObject("geometry"), properties = json.getJSONObject("properties");
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
