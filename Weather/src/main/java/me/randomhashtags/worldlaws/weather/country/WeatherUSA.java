package me.randomhashtags.worldlaws.weather.country;

import me.randomhashtags.worldlaws.*;
import me.randomhashtags.worldlaws.country.Location;
import me.randomhashtags.worldlaws.country.SovereignStateSubdivision;
import me.randomhashtags.worldlaws.country.WLCountry;
import me.randomhashtags.worldlaws.stream.ParallelStream;
import me.randomhashtags.worldlaws.weather.*;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public enum WeatherUSA implements WeatherController {
    INSTANCE;

    private final String zonePrefix;
    private HashMap<String, String> alertIDs, eventPreAlerts, territoryEvents;
    private final ConcurrentHashMap<String, WeatherZone> zones;
    private HashMap<String, HashMap<String, String>> territoryPreAlerts;
    private HashMap<String, WeatherPreAlert> preAlertIDs;

    WeatherUSA() {
        zonePrefix = "https://api\\.weather\\.gov/zones/";
        zones = new ConcurrentHashMap<>();
    }

    @Override
    public WLCountry getCountry() {
        return WLCountry.UNITED_STATES;
    }

    @Override
    public EventSource getSource() {
        return new EventSource("U.S. National Weather Service", "https://www.weather.gov");
    }

    @Override
    public HashMap<String, String> getEventPreAlerts() {
        return eventPreAlerts;
    }

    @Override
    public HashMap<String, String> getSubdivisionEvents() {
        return territoryEvents;
    }

    @Override
    public HashMap<String, HashMap<String, String>> getSubdivisionPreAlerts() {
        return territoryPreAlerts;
    }

    @Override
    public String refresh() {
        final String url = "https://api.weather.gov/alerts/active?status=actual";
        final HashMap<String, String> headers = new HashMap<>(CONTENT_HEADERS);
        headers.put("User-Agent", "(Paradigm Proxy, Weather Module - Java Application, ***REMOVED***)");
        final JSONObject json = requestJSONObject(url, RequestMethod.GET, headers);
        String string = null;
        if(json != null) {
            final JSONArray array = json.getJSONArray("features");
            if(array.length() > 0) {
                alertIDs = new HashMap<>();
                eventPreAlerts = new HashMap<>();
                territoryEvents = new HashMap<>();
                territoryPreAlerts = new HashMap<>();
                preAlertIDs = new HashMap<>();

                final HashSet<String> zoneIDs = new HashSet<>();
                final HashSet<JSONObject> jsons = new HashSet<>();
                ParallelStream.stream(array.spliterator(), obj -> {
                    final JSONObject jsonAlert = (JSONObject) obj;
                    final JSONObject properties = jsonAlert.getJSONObject("properties");
                    final JSONArray affectedZones = properties.getJSONArray("affectedZones");
                    zoneIDs.addAll(getZoneIDs(affectedZones));
                    jsons.add(jsonAlert);
                });
                processZones(zoneIDs);
                string = processAlerts(jsons);
            }
        }
        return string;
    }

    private void processZones(Collection<String> zoneIDs) {
        zoneIDs.removeIf(zoneID -> {
            return zones.containsKey(zoneID) || getLocalZone(zoneID, null) != null;
        });
        final int amount = zoneIDs.size();
        if(amount > 0) {
            final long started = System.currentTimeMillis();
            WLLogger.logInfo("WeatherUSA - loading " + amount + " zones...");
            for(String zoneID : zoneIDs) {
                getWeatherZone(zoneID);
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            WLLogger.logInfo("WeatherUSA - loaded " + amount + " zones (took " + (System.currentTimeMillis()-started) + "ms)");
        }
    }
    private String processAlerts(HashSet<JSONObject> jsons) {
        final WLCountry unitedStates = WLCountry.UNITED_STATES;

        final ConcurrentHashMap<String, Integer> eventsMap = new ConcurrentHashMap<>();
        final ConcurrentHashMap<String, HashSet<WeatherPreAlert>> eventPreAlertsMap = new ConcurrentHashMap<>();
        final ConcurrentHashMap<String, HashSet<WeatherEvent>> subdivisionEventsMap = new ConcurrentHashMap<>();
        final ConcurrentHashMap<String, ConcurrentHashMap<String, HashSet<WeatherPreAlert>>> territoryPreAlertsMap = new ConcurrentHashMap<>();

        ParallelStream.stream(jsons, jsonObj -> {
            final JSONObject json = (JSONObject) jsonObj;
            final String id = json.getString("id").split("/alerts/")[1];
            final JSONObject properties = json.getJSONObject("properties");

            final JSONArray affectedZones = properties.getJSONArray("affectedZones");
            final HashSet<String> zoneIDs = getZoneIDs(affectedZones);
            final HashSet<WeatherZone> zones = new HashSet<>();
            for(String zoneID : zoneIDs) {
                final WeatherZone zone = getWeatherZone(zoneID);
                zones.add(zone);
            }

            final String severityString = properties.getString("severity"), severity = severityString.equals("Unknown") ? "-1" : severityString;
            final String certainty = properties.getString("certainty");
            final String event = properties.getString("event");
            final String headline = properties.has("headline") ? properties.getString("headline") : null;
            final String description = properties.get("description") instanceof String ? properties.getString("description")
                    .replace("\n\n", "%double_bruh%")
                    .replace(".\n", "%bruh%")
                    .replace("\n", " ")
                    .replace("%bruh%", ".\n")
                    .replace("%double_bruh%", "\n\n") : null;
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
            eventsMap.putIfAbsent(event, defcon);


            final HashSet<String> subdivisions = new HashSet<>();
            for(WeatherZone zone : zones) {
                final String subdivisionName = zone.getSubdivision();
                subdivisions.add(subdivisionName);
            }
            final WeatherPreAlert preAlert = new WeatherPreAlert(defcon, event, id, subdivisions, certainty, headline, instruction, description, zones, time);
            preAlertIDs.put(id, preAlert);

            eventPreAlertsMap.putIfAbsent(event, new HashSet<>());
            eventPreAlertsMap.get(event).add(preAlert);

            final String eventLowercase = event.toLowerCase().replace(" ", "");
            final WeatherEvent weatherEvent = new WeatherEvent(event, defcon);
            for(String subdivisionName : subdivisions) {
                subdivisions.add(subdivisionName);

                subdivisionEventsMap.putIfAbsent(subdivisionName, new HashSet<>());
                final HashSet<WeatherEvent> territorySet = subdivisionEventsMap.get(subdivisionName);
                boolean hasEvent = false;
                for(WeatherEvent newWeatherEvent : territorySet) {
                    if(event.equals(newWeatherEvent.getEvent())) {
                        hasEvent = true;
                        break;
                    }
                }
                if(!hasEvent) {
                    subdivisionEventsMap.get(subdivisionName).add(weatherEvent);
                }
                territoryPreAlertsMap.putIfAbsent(subdivisionName, new ConcurrentHashMap<>());
                territoryPreAlertsMap.get(subdivisionName).putIfAbsent(eventLowercase, new HashSet<>());
                territoryPreAlertsMap.get(subdivisionName).get(eventLowercase).add(preAlert);
            }
        });
        putEventPreAlerts(eventPreAlerts, eventPreAlertsMap);
        putSubdivisionEvents(territoryEvents, subdivisionEventsMap);
        putSubdivisionPreAlerts(territoryPreAlerts, territoryPreAlertsMap);
        return getEventsJSON(eventsMap);
    }

    private HashSet<String> getZoneIDs(JSONArray affectedZones) {
        final HashSet<String> zoneIDs = new HashSet<>();
        for(Object object : affectedZones) {
            final String zoneURL = (String) object;
            final String zoneID = zoneURL.split(zonePrefix)[1];
            zoneIDs.add(zoneID);
        }
        return zoneIDs;
    }
    @Override
    public String getAlert(String id) {
        if(alertIDs == null) {
            final String string = refresh();
        }
        return tryGettingAlert(id);
    }
    private String tryGettingAlert(String id) {
        String string = null;
        if(alertIDs.containsKey(id)) {
            string = alertIDs.get(id);
        } else if(preAlertIDs.containsKey(id)) {
            final EventSource source = getSource();
            final WeatherPreAlert preAlert = preAlertIDs.get(id);
            final HashSet<WeatherZone> zones = preAlert.getZones();

            final StringBuilder builder = new StringBuilder("[");
            boolean isFirst = true;
            for(WeatherZone zone : zones) {
                builder.append(isFirst ? "" : ",").append(zone.toString());
                isFirst = false;
            }
            builder.append("]");
            final String zonesJSON = builder.toString();

            final WeatherAlert alert = new WeatherAlert(preAlert, zonesJSON, source);
            string = alert.toString();
            alertIDs.put(id, string);
            preAlertIDs.remove(id);
        }
        return string;
    }

    @Override
    public String getZones(String[] zones) {
        final HashSet<String> zoneJSONs = new HashSet<>();
        ParallelStream.stream(Arrays.asList(zones), zoneID -> {
            final String string = getZone((String) zoneID);
            if(string != null) {
                zoneJSONs.add(string);
            }
        });

        final StringBuilder builder = new StringBuilder("[");
        boolean isFirst = true;
        for(String zoneJSON : zoneJSONs) {
            builder.append(isFirst ? "" : ",").append(zoneJSON);
            isFirst = false;
        }
        builder.append("]");
        return builder.toString();
    }

    private JSONObject getLocalZone(String zoneID, CompletionHandler handler) {
        final String[] values = zoneID.split("/");
        final String zoneType = values[0], zone = values[1], zoneFolder = zone.substring(0, 2);
        return getJSONObject(Folder.WEATHER_USA_ZONES, zoneType + File.separator + zoneFolder + File.separator + zone.substring(2), handler);
    }

    @Override
    public String getZone(String zoneID) {
        if(!zones.containsKey(zoneID)) {
            getWeatherZone(zoneID);
        }
        return zones.containsKey(zoneID) ? zones.get(zoneID).toString() : null;
    }
    private WeatherZone getWeatherZone(String zoneID) {
        final JSONObject json = getLocalZone(zoneID, new CompletionHandler() {
            @Override
            public JSONObject loadJSONObject() {
                final String url = "https://api.weather.gov/zones/" + zoneID;
                return requestJSONObject(url, RequestMethod.GET);
            }
        });

        if(json != null) {
            final JSONObject geometryJSON = json.getJSONObject("geometry"), properties = json.getJSONObject("properties");
            final Object state = properties.get("state");
            final String stateString = state instanceof String ? (String) state : null;
            final SovereignStateSubdivision subdivision = WLCountry.UNITED_STATES.valueOfSovereignStateSubdivision(stateString);
            final String name = properties.getString("name"), territory = subdivision != null ? subdivision.getName() : "Unknown";
            final List<Location> geometry = getGeometry(geometryJSON);
            final WeatherZone weatherZone = new WeatherZone(name, territory, geometry);
            zones.put(zoneID, weatherZone);
        }
        return zones.getOrDefault(zoneID, null);
    }
    private List<Location> getGeometry(JSONObject geometryJSON) {
        final String geometryType = geometryJSON.getString("type");
        final List<Location> geometry = new ArrayList<>();
        switch (geometryType) {
            case "MultiPolygon":
                final JSONArray coordinates = geometryJSON.getJSONArray("coordinates");
                for(Object coordinateArray : coordinates) {
                    final JSONArray array = (JSONArray) coordinateArray;
                    geometry.addAll(getGeometryFromArray(array.getJSONArray(0)));
                }
                return geometry;
            case "Polygon":
                return getGeometryFromArray(geometryJSON.getJSONArray("coordinates").getJSONArray(0));
            case "GeometryCollection":
                final JSONArray array = geometryJSON.getJSONArray("geometries");
                for(Object obj : array) {
                    final JSONObject json = (JSONObject) obj;
                    final List<Location> targetLocations = getGeometry(json);
                    geometry.addAll(targetLocations);
                }
                break;
            default:
                WLLogger.logError(INSTANCE, "getGeometry - uncaught geometryType \"" + geometryType + "\"!");
                break;
        }
        return geometry;
    }
    private List<Location> getGeometryFromArray(JSONArray array) {
        final List<Location> geometry = new ArrayList<>();
        for(Object coordinate : array) {
            final JSONArray coordinateArray = (JSONArray) coordinate;
            final double longitude = coordinateArray.getDouble(0), latitude = coordinateArray.getDouble(1);
            final Location location = new Location(latitude, longitude);
            geometry.add(location);
        }
        return geometry;
    }
}
