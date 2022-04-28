package me.randomhashtags.worldlaws.weather.country;

import me.randomhashtags.worldlaws.*;
import me.randomhashtags.worldlaws.country.Location;
import me.randomhashtags.worldlaws.country.SovereignStateSubdivision;
import me.randomhashtags.worldlaws.country.WLCountry;
import me.randomhashtags.worldlaws.locale.JSONObjectTranslatable;
import me.randomhashtags.worldlaws.stream.CompletableFutures;
import me.randomhashtags.worldlaws.weather.*;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public enum WeatherUSA implements WeatherController {
    INSTANCE;

    private final String zonePrefix;
    private final HashMap<String, WeatherZone> zones;
    private final HashMap<String, JSONObject> forecastOffices;
    private JSONObjectTranslatable previousWeatherAlerts;
    private HashMap<String, JSONObjectTranslatable> alertIDs, eventPreAlerts, territoryEvents;
    private HashMap<String, HashMap<String, JSONObjectTranslatable>> territoryPreAlerts;
    private HashMap<String, WeatherPreAlert> preAlertIDs;

    WeatherUSA() {
        zonePrefix = "https://api\\.weather\\.gov/zones/";
        zones = new HashMap<>();
        forecastOffices = new HashMap<>();
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
    public HashMap<String, JSONObjectTranslatable> getEventPreAlerts() {
        return eventPreAlerts;
    }

    @Override
    public HashMap<String, JSONObjectTranslatable> getSubdivisionEvents() {
        return territoryEvents;
    }

    @Override
    public HashMap<String, HashMap<String, JSONObjectTranslatable>> getSubdivisionPreAlerts() {
        return territoryPreAlerts;
    }

    @Override
    public JSONObjectTranslatable refresh() {
        final String url = "https://api.weather.gov/alerts/active?status=actual";
        final JSONObject json = requestJSONObject(url);
        if(json != null && json.opt("features") instanceof JSONArray) {
            final JSONArray array = json.getJSONArray("features");
            if(array.length() > 0) {
                alertIDs = new HashMap<>();
                eventPreAlerts = new HashMap<>();
                territoryEvents = new HashMap<>();
                territoryPreAlerts = new HashMap<>();
                preAlertIDs = new HashMap<>();

                final HashSet<String> zoneIDs = new HashSet<>();
                new CompletableFutures<JSONObject>().stream(array, jsonAlert -> {
                    final JSONObject properties = jsonAlert.getJSONObject("properties");
                    final JSONArray affectedZones = properties.getJSONArray("affectedZones");
                    final HashSet<String> targetZoneIDs = getZoneIDs(affectedZones);
                    zoneIDs.addAll(targetZoneIDs);
                });
                processZones(zoneIDs);
                previousWeatherAlerts = processAlerts(array);
            }
        }
        return previousWeatherAlerts;
    }

    private void processZones(HashSet<String> zoneIDs) {
        zoneIDs.removeIf(zoneID -> {
            return zones.containsKey(zoneID) || getLocalZone(zoneID, null) != null;
        });
        final int amount = zoneIDs.size();
        if(amount > 0) {
            final long started = System.currentTimeMillis();
            final String suffix = " " + amount + " zone" + (amount == 1 ? "" : "s");
            final HashSet<String> officeIDs = new HashSet<>();
            WLLogger.logInfo("WeatherUSA - loading" + suffix + "...");
            for(String zoneID : zoneIDs) {
                processZone(zoneID, officeIDs);
                // to prevent spam
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            WLLogger.logInfo("WeatherUSA - loaded" + suffix + " (took " + WLUtilities.getElapsedTime(started) + ")");
            if(!officeIDs.isEmpty()) {
                processZoneOffices(officeIDs);
            }
        }
    }
    private void processZone(String zoneID, HashSet<String> officeIDs) {
        final JSONObject zone = getOrLoadWeatherZone(zoneID);
        final JSONObject zoneProperties = zone.optJSONObject("properties", null);
        if(zoneProperties != null && zoneProperties.get("state") == null) {
            final String identifier = zoneProperties.getJSONArray("forecastOffices").getString(0);
            officeIDs.add(identifier);
        }
    }
    private void processZoneOffices(Collection<String> officeIDs) {
        officeIDs.removeIf(identifier -> {
            return forecastOffices.containsKey(identifier) || getLocalOffice(identifier, null) != null;
        });
        final int amount = officeIDs.size();
        if(amount > 0) {
            final long started = System.currentTimeMillis();
            final String suffix = " " + amount + " forecast office" + (amount == 1 ? "" : "s");
            WLLogger.logInfo("WeatherUSA - loading" + suffix + "...");
            for(String identifier : officeIDs) {
                getForecastOffice(identifier);
                // to prevent spam
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            WLLogger.logInfo("WeatherUSA - loaded" + suffix + " (took " + WLUtilities.getElapsedTime(started) + ")");
        }
    }
    private JSONObjectTranslatable processAlerts(JSONArray array) {
        final ConcurrentHashMap<String, Integer> eventsMap = new ConcurrentHashMap<>();
        final ConcurrentHashMap<String, HashSet<WeatherPreAlert>> eventPreAlertsMap = new ConcurrentHashMap<>();
        final ConcurrentHashMap<String, ConcurrentHashMap<String, WeatherEvent>> subdivisionEventsMap = new ConcurrentHashMap<>();
        final ConcurrentHashMap<String, ConcurrentHashMap<String, HashSet<WeatherPreAlert>>> territoryPreAlertsMap = new ConcurrentHashMap<>();

        new CompletableFutures<JSONObject>().stream(array, json -> {
            final String id = json.getString("id").split("/alerts/")[1];
            final JSONObject properties = json.getJSONObject("properties");

            final String severityString = properties.getString("severity"), severity = severityString.equals("Unknown") ? "-1" : severityString;
            final String certainty = properties.getString("certainty");
            final String event = properties.getString("event");
            final String headline = properties.optString("headline", null);
            final String description = properties.optString("description", null);
            final String instruction = properties.optString("instruction", null);
            final String sent = properties.getString("sent");
            final String effective = properties.getString("effective");
            final String expires = properties.getString("expires");
            final String ends = properties.optString("ends", null);
            final WeatherAlertTime time = new WeatherAlertTime(sent, effective, expires, ends);

            final int defcon = getSeverityDEFCON(severity);
            eventsMap.putIfAbsent(event, defcon);

            final JSONArray affectedZones = properties.getJSONArray("affectedZones");
            final HashSet<String> zoneIDs = getZoneIDs(affectedZones);
            final HashSet<WeatherZone> zones = new HashSet<>();
            for(String zoneID : zoneIDs) {
                final WeatherZone zone = getWeatherZone(zoneID);
                if(zone != null) {
                    zones.add(zone);
                }
            }

            final HashSet<String> subdivisions = new HashSet<>();
            for(WeatherZone zone : zones) {
                final String subdivisionName = zone.getSubdivision();
                subdivisions.add(subdivisionName);
            }
            final WeatherPreAlert preAlert = new WeatherPreAlert(defcon, event, id, subdivisions, certainty, headline, instruction, description, zones, time);
            preAlertIDs.put(id, preAlert);

            eventPreAlertsMap.putIfAbsent(event, new HashSet<>());

            final String eventLowercase = event.toLowerCase().replace(" ", "");
            final WeatherEvent weatherEvent = new WeatherEvent(event, defcon);
            for(String subdivisionName : subdivisions) {
                subdivisionEventsMap.putIfAbsent(subdivisionName, new ConcurrentHashMap<>());
                if(!subdivisionEventsMap.get(subdivisionName).containsKey(event)) {
                    subdivisionEventsMap.get(subdivisionName).put(event, weatherEvent);
                }
                territoryPreAlertsMap.putIfAbsent(subdivisionName, new ConcurrentHashMap<>());
                territoryPreAlertsMap.get(subdivisionName).putIfAbsent(eventLowercase, new HashSet<>());

                final WeatherPreAlert subdivisionPreAlert = preAlert.onlyWithSubdivision(subdivisionName);
                territoryPreAlertsMap.get(subdivisionName).get(eventLowercase).add(subdivisionPreAlert);

                eventPreAlertsMap.get(event).add(subdivisionPreAlert);
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
    public JSONObjectTranslatable getAlert(String id) {
        if(alertIDs == null) {
            final JSONObjectTranslatable string = refresh();
        }
        return tryGettingAlert(id);
    }
    private JSONObjectTranslatable tryGettingAlert(String id) {
        JSONObjectTranslatable json = null;
        if(alertIDs.containsKey(id)) {
            json = alertIDs.get(id);
        } else if(preAlertIDs.containsKey(id)) {
            final EventSource source = getSource();
            final WeatherPreAlert preAlert = preAlertIDs.get(id);
            final HashSet<WeatherZone> zones = preAlert.getZones();
            final WeatherAlert alert = new WeatherAlert(preAlert, zones, source);
            json = alert.toJSONObject();
            alertIDs.put(id, json);
            preAlertIDs.remove(id);
        }
        return json;
    }

    private JSONObject getLocalZone(String zoneID, CompletionHandler handler) {
        final String countryBackendID = getCountry().getBackendID();
        final String[] values = zoneID.split("/");
        final String zoneType = values[0], zone = values[1], subdivisionFolder = zone.substring(0, 2);
        final Folder folder = Folder.WEATHER_COUNTRY_ZONES;
        final String fileName = zone.substring(2);
        folder.setCustomFolderName(fileName, folder.getFolderName().replace("%country%", countryBackendID).replace("%type%", zoneType).replace("%subdivision%", subdivisionFolder));
        return getJSONObject(folder, fileName, handler);
    }
    private JSONObject getLocalOffice(String officeID, CompletionHandler handler) {
        final String countryBackendID = getCountry().getBackendID();
        final Folder folder = Folder.WEATHER_COUNTRY_OFFICES;
        folder.setCustomFolderName(officeID, folder.getFolderName().replace("%country%", countryBackendID));
        return getJSONObject(folder, officeID, handler);
    }

    private JSONObject getOrLoadWeatherZone(String zoneID) {
        return getLocalZone(zoneID, new CompletionHandler() {
            @Override
            public JSONObject loadJSONObject() {
                final String url = "https://api.weather.gov/zones/" + zoneID;
                return requestJSONObject(url);
            }
        });
    }
    private WeatherZone getWeatherZone(String zoneID) {
        final JSONObject json = getOrLoadWeatherZone(zoneID);
        if(json != null) {
            final WLCountry unitedStates = getCountry();
            final String nameSuffix = zoneID.startsWith("county") ? " County" : null;
            if(json.has("geometry") && json.has("properties")) {
                final JSONObject geometryJSON = json.getJSONObject("geometry"), properties = json.getJSONObject("properties");
                String stateString = properties.optString("state", null);
                if(stateString == null) {
                    final String officeIdentifier = properties.getJSONArray("forecastOffices").getString(0).substring("https://api.weather.gov/offices/".length());
                    final JSONObject officeJSON = getForecastOffice(officeIdentifier);
                    final JSONObject addressJSON = officeJSON.optJSONObject("address", null);
                    stateString = addressJSON != null ? addressJSON.optString("addressRegion", null) : null;
                }
                SovereignStateSubdivision subdivision = unitedStates.valueOfSovereignStateSubdivision(stateString);
                if(subdivision == null) {
                    subdivision = unitedStates.valueOfSovereignStateSubdivision(zoneID.substring(0, 2));
                }
                if(subdivision == null) {
                    WLLogger.logError("WeatherUSA", "failed to find subdivision for stateString \"" + stateString + "\"! (zoneID=" + zoneID + ")");
                }
                final String name = properties.getString("name");
                final String territory = subdivision != null ? subdivision.getName() : stateString != null ? stateString : "Unknown";
                final List<Location> geometry = getGeometry(geometryJSON);
                final WeatherZone weatherZone = new WeatherZone(name, nameSuffix, territory, geometry);
                zones.put(zoneID, weatherZone);
            }
        }
        return zones.getOrDefault(zoneID, null);
    }
    private JSONObject getForecastOffice(String identifier) {
        if(!forecastOffices.containsKey(identifier)) {
            final String url = "https://api.weather.gov/offices/" + identifier;
            final JSONObject json = getLocalOffice(identifier, new CompletionHandler() {
                @Override
                public JSONObject loadJSONObject() {
                    return requestJSONObject(url);
                }
            });
            forecastOffices.put(identifier, json);
        }
        return forecastOffices.get(identifier);
    }
    private List<Location> getGeometry(JSONObject geometryJSON) {
        final List<Location> geometry = new ArrayList<>();
        if(geometryJSON != null) {
            final String geometryType = geometryJSON.getString("type");
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
