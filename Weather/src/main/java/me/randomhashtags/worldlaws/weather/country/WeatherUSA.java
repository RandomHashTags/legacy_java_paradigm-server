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
    private final ConcurrentHashMap<String, String> zones;
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
        alertIDs = new HashMap<>();
        eventPreAlerts = new HashMap<>();
        territoryEvents = new HashMap<>();
        territoryPreAlerts = new HashMap<>();
        preAlertIDs = new HashMap<>();

        final String url = "https://api.weather.gov/alerts/active?status=actual";
        final HashMap<String, String> headers = new HashMap<>(CONTENT_HEADERS);
        headers.put("User-Agent", "(Paradigm Weather Module - Java Application, ***REMOVED***)");
        final JSONObject json = requestJSONObject(url, RequestMethod.GET, headers);
        String string = null;
        if(json != null) {
            final JSONArray array = json.getJSONArray("features");
            if(array.length() > 0) {
                final HashSet<String> zoneIDs = new HashSet<>();
                final HashSet<JSONObject> jsons = new HashSet<>();
                ParallelStream.stream(array.spliterator(), obj -> {
                    final JSONObject jsonAlert = array.getJSONObject(0);
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
        zoneIDs.removeIf(zones::containsKey);
        final int amount = zoneIDs.size();
        final long started = System.currentTimeMillis();
        WLLogger.logInfo("WeatherUSA - loading " + amount + " zones...");
        /*ParallelStream.stream(zoneIDs, zoneIDObj -> {
            final String zoneID = (String) zoneIDObj;
            getZone(zoneID);
        });*/
        WLLogger.logInfo("WeatherUSA - loaded " + amount + " zones (took " + (System.currentTimeMillis()-started) + "ms)");
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

            final String senderName = properties.getString("senderName");
            final String[] senderNameValues = senderName.split(" ");
            final int senderNameLength = senderNameValues.length;
            final String territoryAbbreviation = senderNameValues[senderNameLength-1];
            final SovereignStateSubdivision subdivision = unitedStates.valueOfSovereignStateSubdivision(territoryAbbreviation);
            final String subdivisionName = subdivision != null ? subdivision.getName() : "Unknown";
            if(subdivisionName.equals("Unknown")) {
                WLLogger.logError(INSTANCE, "refresh - json != null - failed to find subdivision with string \"" + territoryAbbreviation + "\" from sender \"" + senderName + "\"!");
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
                final WeatherEvent weatherEvent = new WeatherEvent(event, defcon);
                subdivisionEventsMap.get(subdivisionName).add(weatherEvent);
            }

            final WeatherPreAlert preAlert = new WeatherPreAlert(defcon, event, id, subdivisionName, certainty, headline, instruction, description, zoneIDs, time);
            preAlertIDs.put(id, preAlert);

            eventPreAlertsMap.putIfAbsent(event, new HashSet<>());
            eventPreAlertsMap.get(event).add(preAlert);

            final String eventLowercase = event.toLowerCase().replace(" ", "");
            territoryPreAlertsMap.putIfAbsent(subdivisionName, new ConcurrentHashMap<>());
            territoryPreAlertsMap.get(subdivisionName).putIfAbsent(eventLowercase, new HashSet<>());
            territoryPreAlertsMap.get(subdivisionName).get(eventLowercase).add(preAlert);
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
            final HashSet<String> zones = preAlert.getZoneIDs(), values = new HashSet<>();
            ParallelStream.stream(zones, zoneID -> {
                final String value = getZone((String) zoneID);
                if(value != null) {
                    values.add(value);
                }
            });

            final StringBuilder builder = new StringBuilder("[");
            boolean isFirst = true;
            for(String value : values) {
                builder.append(isFirst ? "" : ",").append(value);
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

    @Override
    public String getZone(String zoneID) {
        if(!zones.contains(zoneID)) {
            final String[] values = zoneID.split("/");
            final String zoneType = values[0], zone = values[1], zoneFolder = zone.substring(0, 2);
            final JSONObject json = getJSONObject(Folder.WEATHER_USA_ZONES, zoneType + File.separator + zoneFolder + File.separator + zone.substring(2), new CompletionHandler() {
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
                final WeatherZone weatherZone = new WeatherZone(zoneID, name, territory, geometry);
                final String string = weatherZone.toString();
                zones.put(zoneID, string);
            }
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
