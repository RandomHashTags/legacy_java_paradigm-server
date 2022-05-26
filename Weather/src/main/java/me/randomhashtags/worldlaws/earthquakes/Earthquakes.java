package me.randomhashtags.worldlaws.earthquakes;

import me.randomhashtags.worldlaws.*;
import me.randomhashtags.worldlaws.country.Location;
import me.randomhashtags.worldlaws.country.SovereignStateSubdivision;
import me.randomhashtags.worldlaws.country.WLCountry;
import me.randomhashtags.worldlaws.country.WLSubdivisions;
import me.randomhashtags.worldlaws.locale.JSONObjectTranslatable;
import me.randomhashtags.worldlaws.stream.CompletableFutures;
import org.json.JSONArray;
import org.json.JSONObject;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public enum Earthquakes implements RestAPI {
    INSTANCE;

    private JSONObjectTranslatable recentEarthquakes, topRecentEarthquakes;
    private HashMap<String, JSONObjectTranslatable> recentTerritoryEarthquakes, topRecentTerritoryEarthquakes;
    private final HashMap<String, JSONObjectTranslatable> cachedEarthquakes;

    Earthquakes() {
        cachedEarthquakes = new HashMap<>();
    }

    public void clearCachedEarthquakes() {
        cachedEarthquakes.clear();
    }

    public JSONObjectTranslatable getResponse(String[] values) {
        final String key = values[0];
        switch (key) {
            case "recent":
                return getFromTerritory(true, values.length == 1 ? null : values[1]);
            case "top":
                return getFromTerritory(false, values.length == 1 ? null : values[1]);
            case "id":
                final String id = values[1];
                return getEarthquake(id);
            default:
                return null;
        }
    }

    private JSONObjectTranslatable getFromTerritory(boolean isRecent, String territory) {
        final JSONObjectTranslatable string = isRecent ? recentEarthquakes : topRecentEarthquakes;
        if(string == null) {
            refresh(false, isRecent);
        }
        return getValue(isRecent, territory);
    }
    private JSONObjectTranslatable getValue(boolean isRecent, String territory) {
        final JSONObjectTranslatable target = territory == null ? (isRecent ? recentEarthquakes : topRecentEarthquakes) : (isRecent ? recentTerritoryEarthquakes : topRecentTerritoryEarthquakes).getOrDefault(territory, null);
        return target == null || target.isEmpty() ? null : target;
    }

    private String getURLRequest(LocalDate startDate, LocalDate endDate, float minMagnitude) {
        final EventDate startEventDate = new EventDate(startDate);
        final String startDateString = getEarthquakeEventDateString(startEventDate);
        final EventDate endEventDate = new EventDate(endDate);
        final String endDateString = getEarthquakeEventDateString(endEventDate);
        return "https://earthquake.usgs.gov/fdsnws/event/1/query?format=geojson&starttime=" + startDateString + "&endtime=" + endDateString + "&minmagnitude=" + minMagnitude;
    }
    private String getEarthquakeEventDateString(EventDate date) {
        final int month = date.getMonth().getValue(), day = date.getDay();
        return date.getYear() + "-" + (month < 10 ? "0" : "") + month + "-" + (day < 10 ? "0" : "") + day;
    }

    public void refresh(boolean isAutoUpdate, boolean isRecent) {
        final long started = System.currentTimeMillis();
        final LocalDate now = Instant.ofEpochMilli(started).atZone(ZoneId.ofOffset("", ZoneOffset.UTC)).toLocalDate(), startDate = now.minusDays(30), recentStartingDate = now.minusDays(7);
        final String url = getURLRequest(startDate, now, 2.0f);

        recentTerritoryEarthquakes = new HashMap<>();
        topRecentTerritoryEarthquakes = new HashMap<>();

        final JSONObject json = requestJSONObject(url);
        if(json != null) {
            final JSONArray array = json.getJSONArray("features");
            final ConcurrentHashMap<String, HashSet<PreEarthquake>> territoryEarthquakesMap = new ConcurrentHashMap<>();
            final ConcurrentHashMap<String, ConcurrentHashMap<String, HashSet<PreEarthquake>>> preEarthquakeDates = new ConcurrentHashMap<>();
            new CompletableFutures<JSONObject>().stream(array, earthquakeJSON -> {
                loadEarthquake(startDate, earthquakeJSON, preEarthquakeDates, territoryEarthquakesMap);
            });
            topRecentEarthquakes = getEarthquakesJSON(null, preEarthquakeDates);
            recentEarthquakes = getEarthquakesJSON(recentStartingDate, preEarthquakeDates);
            loadTerritoryEarthquakes(territoryEarthquakesMap);
            completeRefresh(isAutoUpdate, started, isRecent ? recentEarthquakes : topRecentEarthquakes);
        } else {
            completeRefresh(isAutoUpdate, started, null);
        }
    }
    private JSONObjectTranslatable getEarthquakesJSON(LocalDate date, ConcurrentHashMap<String, ConcurrentHashMap<String, HashSet<PreEarthquake>>> preEarthquakeDates) {
        JSONObjectTranslatable json = null;
        if(!preEarthquakeDates.isEmpty()) {
            json = new JSONObjectTranslatable();
            final boolean doesntHaveDate = date == null;
            for(Map.Entry<String, ConcurrentHashMap<String, HashSet<PreEarthquake>>> map : preEarthquakeDates.entrySet()) {
                final String dateString = map.getKey();
                if(doesntHaveDate || EventDate.valueOfDateString(dateString).getLocalDate().isAfter(date)) {
                    final JSONObjectTranslatable quakesJSON = new JSONObjectTranslatable();
                    final ConcurrentHashMap<String, HashSet<PreEarthquake>> value = map.getValue();
                    for(Map.Entry<String, HashSet<PreEarthquake>> map2 : value.entrySet()) {
                        final JSONObjectTranslatable magnitudeJSON = new JSONObjectTranslatable();
                        final String magnitude = map2.getKey();
                        final HashSet<PreEarthquake> preEarthquakes = map2.getValue();
                        for(PreEarthquake preEarthquake : preEarthquakes) {
                            final String id = preEarthquake.getID();
                            magnitudeJSON.put(id, preEarthquake.toJSONObject(), true);
                        }
                        quakesJSON.put(magnitude, magnitudeJSON, true);
                    }
                    json.put(dateString, quakesJSON, true);
                }
            }
        }
        return json;
    }
    private void completeRefresh(boolean isAutoUpdate, long started, JSONObjectTranslatable string) {
        WLLogger.logInfo("Earthquakes - " + (isAutoUpdate ? "auto-" : "") + "refreshed (took " + WLUtilities.getElapsedTime(started) + ")");
    }

    private void loadEarthquake(LocalDate startingDate, JSONObject json, ConcurrentHashMap<String, ConcurrentHashMap<String, HashSet<PreEarthquake>>> preEarthquakeDates, ConcurrentHashMap<String, HashSet<PreEarthquake>> territoryEarthquakesMap) {
        final JSONObject properties = json.getJSONObject("properties");
        final long time = properties.getLong("time");
        final EventDate date = new EventDate(time);
        if(date.getLocalDate().isAfter(startingDate)) {
            final Object magnitudeObj = properties.has("mag") ? properties.get("mag") : null;
            final String magnitude = magnitudeObj != null && !magnitudeObj.toString().equals("null") ? magnitudeObj.toString() : "0.00";
            final String id = json.getString("id");
            final JSONObject geometry = json.getJSONObject("geometry");
            final JSONArray coordinates = geometry.getJSONArray("coordinates");
            final boolean isPoint = geometry.getString("type").equalsIgnoreCase("point");
            final double latitude = isPoint ? coordinates.getDouble(1) : -1, longitude = isPoint ? coordinates.getDouble(0) : -1;
            final Location location = new Location(latitude, longitude);

            String place = properties.optString("place", "null");

            final String[] regionValues = getRegionValues(place);
            place = regionValues[0];
            final String country = regionValues[1], subdivision = regionValues[2];

            final String dateString = date.getDateString();
            final PreEarthquake preEarthquake = new PreEarthquake(time, id, place, country, subdivision, location);
            preEarthquakeDates.putIfAbsent(dateString, new ConcurrentHashMap<>());
            preEarthquakeDates.get(dateString).putIfAbsent(magnitude, new HashSet<>());
            preEarthquakeDates.get(dateString).get(magnitude).add(preEarthquake);

            if(country != null) {
                territoryEarthquakesMap.putIfAbsent(country, new HashSet<>());
                territoryEarthquakesMap.get(country).add(preEarthquake);
            }
            if(subdivision != null) {
                territoryEarthquakesMap.putIfAbsent(subdivision, new HashSet<>());
                territoryEarthquakesMap.get(subdivision).add(preEarthquake);
            }
        }
    }
    private void loadTerritoryEarthquakes(ConcurrentHashMap<String, HashSet<PreEarthquake>> territoryEarthquakesMap) {
        for(Map.Entry<String, HashSet<PreEarthquake>> territoryEarthquakeMap : territoryEarthquakesMap.entrySet()) {
            final String territory = territoryEarthquakeMap.getKey();
            final HashSet<PreEarthquake> territoryEarthquakes = territoryEarthquakeMap.getValue();
            final JSONObjectTranslatable json = new JSONObjectTranslatable();
            for(PreEarthquake earthquake : territoryEarthquakes) {
                final String id = earthquake.getID();
                json.put(id, earthquake.toJSONObject(), true);
            }
            recentTerritoryEarthquakes.put(territory, json);
            topRecentTerritoryEarthquakes.put(territory, json);
        }
    }

    private JSONObjectTranslatable getEarthquake(String id) {
        if(!cachedEarthquakes.containsKey(id)) {
            final JSONObject json = requestJSONObject("https://earthquake.usgs.gov/fdsnws/event/1/query?eventid=" + id + "&format=geojson");
            if(json != null) {
                final JSONObject properties = json.getJSONObject("properties");
                final Object mag = properties.get("mag");
                final float magnitude = Float.parseFloat(mag != null ? mag.toString() : "0");

                final JSONObject geometry = json.getJSONObject("geometry");
                final JSONArray coordinates = geometry.getJSONArray("coordinates");
                final boolean isPoint = geometry.getString("type").equalsIgnoreCase("point");
                final double latitude = isPoint ? coordinates.getDouble(1) : -1, longitude = isPoint ? coordinates.getDouble(0) : -1;
                final Location location = new Location(latitude, longitude);

                final String url = properties.getString("url"), cause = properties.getString("type").toUpperCase();

                String place = properties.optString("place", "null");
                final String[] regionValues = getRegionValues(place);
                place = regionValues[0];
                final String country = regionValues[1], subdivision = regionValues[2];

                final JSONObject productsJSON = properties.optJSONObject("products", null);
                float depthKM = -1;
                if(productsJSON != null) {
                    final JSONArray origin = productsJSON.optJSONArray("origin");
                    if(origin != null) {
                        final JSONObject targetJSON = origin.getJSONObject(0);
                        final JSONObject targetProperties = targetJSON.optJSONObject("properties", null);
                        if(targetProperties != null) {
                            depthKM = Float.parseFloat(targetProperties.getString("depth"));
                        }
                    }
                }

                final long time = properties.getLong("time"), lastUpdated = properties.getLong("updated");
                final EventSources sources = new EventSources();
                sources.add(new EventSource("United States Geological Survey: Earthquakes", url));
                final Earthquake earthquake = new Earthquake(country, subdivision, cause, magnitude, place, time, lastUpdated, depthKM, location, sources);
                cachedEarthquakes.put(id, earthquake.toJSONObject());
            }
        }
        return cachedEarthquakes.get(id);
    }

    private String[] getRegionValues(String place) {
        String country = null, territory = null;
        final String[] values = place.split(", ");
        final String targetValue = values[values.length-1];
        final String targetValueLowercase = targetValue.toLowerCase();
        final String target = targetValueLowercase.replace(" region", "").replace(" ", "");
        WLCountry wlcountry = WLCountry.valueOfString(target);
        if(wlcountry != null) {
            country = target;
        } else {
            final SovereignStateSubdivision subdivision = WLSubdivisions.valueOfString(target);
            if(subdivision != null) {
                country = subdivision.getCountry().getBackendID();
                territory = subdivision.getBackendID();
            }
        }
        if(country != null && !place.equals(targetValue)) {
            place = place.substring(0, place.length()-targetValue.length()-2);
        }
        return new String[] { place, country, territory };
    }
}
