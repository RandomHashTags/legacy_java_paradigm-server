package me.randomhashtags.worldlaws.weather.country;

import me.randomhashtags.worldlaws.*;
import me.randomhashtags.worldlaws.country.Location;
import me.randomhashtags.worldlaws.country.SovereignStateSubdivision;
import me.randomhashtags.worldlaws.country.WLCountry;
import me.randomhashtags.worldlaws.locale.JSONArrayTranslatable;
import me.randomhashtags.worldlaws.locale.JSONObjectTranslatable;
import me.randomhashtags.worldlaws.notifications.RemoteNotification;
import me.randomhashtags.worldlaws.notifications.RemoteNotificationConditionalValue;
import me.randomhashtags.worldlaws.notifications.RemoteNotificationSubcategory;
import me.randomhashtags.worldlaws.notifications.subcategory.RemoteNotificationSubcategoryWeather;
import me.randomhashtags.worldlaws.stream.CompletableFutures;
import me.randomhashtags.worldlaws.weather.*;
import org.json.JSONArray;
import org.json.JSONObject;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public enum WeatherUSA implements WeatherController {
    INSTANCE;

    private final String zonePrefix;
    private final ConcurrentHashMap<String, WeatherZone> zones;
    private final ConcurrentHashMap<String, JSONObject> forecastOffices;
    private JSONObjectTranslatable previousWeatherAlerts;
    private HashMap<String, JSONObjectTranslatable> alertIDs, eventPreAlerts, territoryEvents;
    private HashMap<String, HashMap<String, JSONObjectTranslatable>> territoryPreAlerts;
    private HashMap<String, WeatherPreAlert> preAlertIDs;

    WeatherUSA() {
        zonePrefix = "https://api\\.weather\\.gov/zones/";
        zones = new ConcurrentHashMap<>();
        forecastOffices = new ConcurrentHashMap<>();
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
    public JSONArrayTranslatable getEventTypes() {
        final String countryBackendID = getCountry().getBackendID();
        final Folder folder = Folder.WEATHER_COUNTRY;
        final String fileName = "eventTypes";
        folder.setCustomFolderName(fileName, folder.getFolderName().replace("%country%", countryBackendID));
        final JSONArrayTranslatable array = new JSONArrayTranslatable();
        final JSONArray local = getLocalFileJSONArray(folder, fileName);
        if(local != null) {
            array.putAll(local);
        } else {
            final String url = "https://api.weather.gov/alerts/types";
            final JSONObject json = requestJSONObject(url);
            if(json != null && json.has("eventTypes")) {
                final JSONArray eventTypesArray = json.getJSONArray("eventTypes");
                eventTypesArray.put("All Alerts");
                array.putAll(eventTypesArray);
            }
        }
        return array;
    }

    @Override
    public JSONObjectTranslatable refresh() {
        final long now = System.currentTimeMillis();
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
                previousWeatherAlerts = processAlerts(now, array);
            }
        }
        return previousWeatherAlerts;
    }

    private void processZones(HashSet<String> zoneIDs) {
        final String countryBackendID = getCountry().getBackendID();
        zoneIDs.removeIf(zoneID -> {
            return zones.containsKey(zoneID) || getLocalZone(countryBackendID, zoneID) != null;
        });
        final int amount = zoneIDs.size();
        if(amount > 0) {
            final long started = System.currentTimeMillis();
            final String suffix = " " + amount + " zone" + (amount == 1 ? "" : "s");
            final HashSet<String> officeIDs = new HashSet<>();
            WLLogger.logInfo("WeatherUSA - loading" + suffix + "...");
            for(String zoneID : zoneIDs) {
                loadZone(countryBackendID, zoneID, officeIDs);
                // to prevent spam
                try {
                    Thread.sleep(500);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            WLLogger.logInfo("WeatherUSA - loaded" + suffix + " (took " + WLUtilities.getElapsedTime(started) + ")");
            if(!officeIDs.isEmpty()) {
                processZoneOffices(countryBackendID, officeIDs);
            }
        }
    }

    private void processZoneOffices(String countryBackendID, Collection<String> officeIDs) {
        officeIDs.removeIf(identifier -> {
            return forecastOffices.containsKey(identifier) || getLocalOffice(countryBackendID, identifier) != null;
        });
        final int amount = officeIDs.size();
        if(amount > 0) {
            final long started = System.currentTimeMillis();
            final String suffix = " " + amount + " forecast office" + (amount == 1 ? "" : "s");
            WLLogger.logInfo("WeatherUSA - loading" + suffix + "...");
            for(String identifier : officeIDs) {
                loadOffice(countryBackendID, identifier);
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
    private JSONObjectTranslatable processAlerts(long now, JSONArray array) {
        final ConcurrentHashMap<String, Integer> eventsMap = new ConcurrentHashMap<>();
        final ConcurrentHashMap<String, HashSet<WeatherPreAlert>> eventPreAlertsMap = new ConcurrentHashMap<>();
        final ConcurrentHashMap<String, ConcurrentHashMap<String, WeatherEvent>> subdivisionEventsMap = new ConcurrentHashMap<>();
        final ConcurrentHashMap<String, ConcurrentHashMap<String, HashSet<WeatherPreAlert>>> territoryPreAlertsMap = new ConcurrentHashMap<>();

        final ConcurrentHashMap<String, HashSet<String>> remoteNotificationZoneAlerts = new ConcurrentHashMap<>();
        final long remoteNotificationThreshold = TimeUnit.MINUTES.toMillis(10);

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
            final boolean sendsRemoteNotification = now - time.getSent() < remoteNotificationThreshold;

            final int defcon = getSeverityDEFCON(severity);
            eventsMap.putIfAbsent(event, defcon);

            final JSONArray affectedZones = properties.getJSONArray("affectedZones");
            final HashSet<String> zoneIDs = getZoneIDs(affectedZones);
            final HashSet<WeatherZone> zones = new HashSet<>();
            for(String zoneID : zoneIDs) {
                final WeatherZone zone = this.zones.getOrDefault(zoneID, null);
                if(zone != null) {
                    zones.add(zone);
                    if(sendsRemoteNotification) {
                        remoteNotificationZoneAlerts.putIfAbsent(zoneID, new HashSet<>());
                        remoteNotificationZoneAlerts.get(zoneID).add(headline + "||" + event + "||" + id);
                    }
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

        if(!remoteNotificationZoneAlerts.isEmpty()) {
            final String countryBackendID = getCountry().getBackendID();
            final String pathPrefix = APIVersion.getLatest().name() + "/weather/alerts/country/" + countryBackendID + "/id/";
            final RemoteNotificationSubcategory subcategory = RemoteNotificationSubcategoryWeather.LOCAL_ALERT;
            final HashSet<RemoteNotification> notifications = new HashSet<>();
            for(Map.Entry<String, HashSet<String>> entry : remoteNotificationZoneAlerts.entrySet()) {
                final String zoneID = entry.getKey();
                final HashSet<String> events = entry.getValue();
                for(String string : events) {
                    final String[] values = string.split("\\|\\|");
                    final String headline = values[0], event = values[1], id = values[2];
                    final RemoteNotificationConditionalValue conditionalValue = new RemoteNotificationConditionalValue(countryBackendID, zoneID, event);
                    final String body = !headline.equals("null") ? headline : "Was just issued for local zone \"" + zoneID + "\"";
                    final RemoteNotification notification = new RemoteNotification(subcategory, false, event, body, pathPrefix + id, conditionalValue);
                    notifications.add(notification);
                }
            }
            RemoteNotification.push(notifications);
        }
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

    private JSONObject getLocalZone(String countryBackendID, String zoneID) {
        final String[] values = zoneID.split("/");
        final String zoneType = values[0], zone = values[1], subdivisionFolder = zone.substring(0, 2);
        final Folder folder = Folder.WEATHER_COUNTRY_ZONES;
        final String fileName = zone.substring(2);
        folder.setCustomFolderName(fileName, folder.getFolderName().replace("%country%", countryBackendID).replace("%type%", zoneType).replace("%subdivision%", subdivisionFolder));
        JSONObject json = getLocalJSON(folder, fileName);
        if(json != null) {
            json = parseWeatherZone(countryBackendID, zoneID, json);
        }
        return json;
    }
    private void loadZone(String countryBackendID, String zoneID, HashSet<String> officeIDs) {
        final String url = "https://api.weather.gov/zones/" + zoneID;
        final JSONObject json = requestJSONObject(url);
        final JSONObject zoneJSON = json != null && json.optInt("status", -1) != 500 ? json : null;
        if(zoneJSON != null) {
            final JSONObject zoneProperties = zoneJSON.optJSONObject("properties", null);
            if(zoneProperties != null && zoneProperties.get("state") == null) {
                final String identifier = zoneProperties.getJSONArray("forecastOffices").getString(0);
                officeIDs.add(identifier);
            }

            final String[] values = zoneID.split("/");
            final String zoneType = values[0], zone = values[1], subdivisionFolder = zone.substring(0, 2);
            final Folder folder = Folder.WEATHER_COUNTRY_ZONES;
            final String fileName = zone.substring(2);
            folder.setCustomFolderName(fileName, folder.getFolderName().replace("%country%", countryBackendID).replace("%type%", zoneType).replace("%subdivision%", subdivisionFolder));
            Jsonable.setFileJSONObject(folder, fileName, json);
        } else {
            WLUtilities.saveLoggedError("WeatherUSA", "loadZone;zoneID=" + zoneID + ";zone==null;json=\n" + (json != null ? json.toString() : "null"));
        }
    }
    private void loadOffice(String countryBackendID, String officeID) {
        final String url = "https://api.weather.gov/offices/" + officeID;
        final JSONObject json = requestJSONObject(url);
        if(json != null && json.optInt("status", -1) != 500) {
            forecastOffices.put(officeID, json);

            final Folder folder = Folder.WEATHER_COUNTRY_OFFICES;
            folder.setCustomFolderName(officeID, folder.getFolderName().replace("%country%", countryBackendID));
            Jsonable.setFileJSONObject(folder, officeID, json);
        }
    }
    private JSONObject getLocalOffice(String countryBackendID, String officeID) {
        final Folder folder = Folder.WEATHER_COUNTRY_OFFICES;
        folder.setCustomFolderName(officeID, folder.getFolderName().replace("%country%", countryBackendID));
        final JSONObject json = getLocalJSON(folder, officeID);
        if(json != null) {
            forecastOffices.put(officeID, json);
        }
        return json;
    }

    private JSONObject getLocalJSON(Folder folder, String identifier) {
        JSONObject json = Jsonable.getStaticJSONObject(folder, identifier, null);
        if(json != null && json.optInt("status", -1) == 500) {
            final String filePath = Jsonable.getFilePath(folder, identifier, "json");
            final Path path = Paths.get(filePath);
            if(Files.exists(path)) {
                try {
                    Files.delete(path);
                } catch (Exception e) {
                    WLUtilities.saveException(e);
                }
            }
            json = null;
        }
        folder.removeCustomFolderName(identifier);
        return json;
    }

    private JSONObject parseWeatherZone(String countryBackendID, String zoneID, JSONObject json) {
        final String nameSuffix = zoneID.startsWith("county") ? " County" : null;
        final JSONObject geometryJSON = json.optJSONObject("geometry", null), propertiesJSON = json.optJSONObject("properties", null);
        if(geometryJSON != null && propertiesJSON != null) {
            String stateString = propertiesJSON.optString("state", null);
            if(stateString == null) {
                final String officeIdentifier = propertiesJSON.getJSONArray("forecastOffices").getString(0).substring("https://api.weather.gov/offices/".length());
                final JSONObject officeJSON = getLocalOffice(countryBackendID, officeIdentifier);
                final JSONObject addressJSON = officeJSON != null ? officeJSON.optJSONObject("address", null) : null;
                stateString = addressJSON != null ? addressJSON.optString("addressRegion", null) : null;
            }
            final WLCountry unitedStates = getCountry();
            SovereignStateSubdivision subdivision = unitedStates.valueOfSovereignStateSubdivision(stateString);
            if(subdivision == null) {
                subdivision = unitedStates.valueOfSovereignStateSubdivision(zoneID.substring(0, 2));
            }
            if(subdivision == null) {
                WLLogger.logError("WeatherUSA", "failed to find subdivision for stateString \"" + stateString + "\"! (zoneID=" + zoneID + ")");
            }
            final String name = propertiesJSON.getString("name");
            final String territory = subdivision != null ? subdivision.getName() : stateString != null ? stateString : "Unknown";
            final List<Location> geometry = getGeometry(geometryJSON);
            final WeatherZone zone = new WeatherZone(name, nameSuffix, territory, geometry);
            zones.put(zoneID, zone);
            return json;
        }
        return null;
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
