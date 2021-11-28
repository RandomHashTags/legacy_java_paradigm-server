package me.randomhashtags.worldlaws.earthquakes;

import me.randomhashtags.worldlaws.*;
import me.randomhashtags.worldlaws.country.Location;
import me.randomhashtags.worldlaws.country.SovereignStateSubdivision;
import me.randomhashtags.worldlaws.country.WLCountry;
import me.randomhashtags.worldlaws.country.WLSubdivisions;
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
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.StreamSupport;

public enum Earthquakes implements RestAPI {
    INSTANCE;

    private String recentEarthquakes, topRecentEarthquakes;
    private HashMap<String, String> recentTerritoryEarthquakes, topRecentTerritoryEarthquakes;

    public void getResponse(String[] values, CompletionHandler handler) {
        final String key = values[0];
        switch (key) {
            case "recent":
                getFromTerritory(true, values.length == 1 ? null : values[1], handler);
                break;
            case "top":
                getFromTerritory(false, values.length == 1 ? null : values[1], handler);
                break;
            case "id":
                final String id = values[1];
                getEarthquake(id, handler);
                break;
            default:
                handler.handleString(null);
                break;
        }
    }

    private void getFromTerritory(boolean isRecent, String territory, CompletionHandler handler) {
        final String string = isRecent ? recentEarthquakes : topRecentEarthquakes;
        if(string != null) {
            final String value = getValue(isRecent, territory);
            handler.handleString(value);
        } else {
            Weather.INSTANCE.registerFixedTimer(WLUtilities.WEATHER_EARTHQUAKES_UPDATE_INTERVAL, new CompletionHandler() {
                @Override
                public void handleObject(Object object) {
                    refresh(true, false, null);
                }
            });

            final CompletionHandler completionHandler = new CompletionHandler() {
                @Override
                public void handleString(String string) {
                    final String value = getValue(isRecent, territory);
                    handler.handleString(value);
                }
            };
            refresh(false, isRecent, completionHandler);
        }
    }
    private String getValue(boolean isRecent, String territory) {
        final String target = territory == null ? (isRecent ? recentEarthquakes : topRecentEarthquakes) : (isRecent ? recentTerritoryEarthquakes : topRecentTerritoryEarthquakes).getOrDefault(territory, "{}");
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
        return date.getYear() + "-" + (month < 10 ? "0" + month : month) + "-" + (day < 10 ? "0" + day : day);
    }

    private void refresh(boolean isAutoUpdate, boolean isRecent, CompletionHandler handler) {
        final long started = System.currentTimeMillis();
        final LocalDate now = Instant.ofEpochMilli(started).atZone(ZoneId.ofOffset("", ZoneOffset.UTC)).toLocalDate(), startDate = now.minusDays(30), recentStartingDate = now.minusDays(7);
        final String url = getURLRequest(startDate, now, 2.0f);

        recentTerritoryEarthquakes = new HashMap<>();
        topRecentTerritoryEarthquakes = new HashMap<>();

        requestJSONObject(url, RequestMethod.GET, new CompletionHandler() {
            @Override
            public void handleJSONObject(JSONObject json) {
                if(json != null) {
                    final JSONArray array = json.getJSONArray("features");
                    final ConcurrentHashMap<String, HashSet<PreEarthquake>> territoryEarthquakesMap = new ConcurrentHashMap<>();
                    final ConcurrentHashMap<String, ConcurrentHashMap<String, HashSet<String>>> preEarthquakeDates = new ConcurrentHashMap<>();
                    StreamSupport.stream(array.spliterator(), true).forEach(obj -> {
                        final JSONObject earthquakeJSON = (JSONObject) obj;
                        loadEarthquake(startDate, earthquakeJSON, preEarthquakeDates, territoryEarthquakesMap);
                    });
                    topRecentEarthquakes = getEarthquakesJSON(null, preEarthquakeDates);
                    recentEarthquakes = getEarthquakesJSON(recentStartingDate, preEarthquakeDates);
                    loadTerritoryEarthquakes(territoryEarthquakesMap);
                    completeRefresh(isAutoUpdate, started, isRecent ? recentEarthquakes : topRecentEarthquakes, handler);
                } else {
                    completeRefresh(isAutoUpdate, started, null, handler);
                }
            }
        });
    }
    private String getEarthquakesJSON(LocalDate date, ConcurrentHashMap<String, ConcurrentHashMap<String, HashSet<String>>> preEarthquakeDates) {
        String string = null;
        if(!preEarthquakeDates.isEmpty()) {
            final boolean doesntHaveDate = date == null;
            final StringBuilder builder = new StringBuilder("{");
            boolean isFirst = true;
            final AtomicInteger count = new AtomicInteger(0);
            for(Map.Entry<String, ConcurrentHashMap<String, HashSet<String>>> map : preEarthquakeDates.entrySet()) {
                final String dateString = map.getKey();
                if(doesntHaveDate || EventDate.valueOfDateString(dateString).getLocalDate().isAfter(date)) {
                    builder.append(isFirst ? "" : ",").append("\"").append(dateString).append("\":{");
                    final ConcurrentHashMap<String, HashSet<String>> value = map.getValue();
                    boolean isFirstMagnitude = true;
                    count.addAndGet(value.size());
                    for(Map.Entry<String, HashSet<String>> map2 : value.entrySet()) {
                        final String magnitude = map2.getKey();
                        builder.append(isFirstMagnitude ? "" : ",").append("\"").append(magnitude).append("\"").append(":{");
                        final HashSet<String> preEarthquakes = map2.getValue();
                        boolean isFirstPreEarthquake = true;
                        for(String preEarthquake : preEarthquakes) {
                            builder.append(isFirstPreEarthquake ? "" : ",").append(preEarthquake);
                            isFirstPreEarthquake = false;
                        }
                        builder.append("}");
                        isFirstMagnitude = false;
                    }
                    builder.append("}");
                    isFirst = false;
                }
            }
            builder.append("}");
            string = builder.toString();
        }
        return string;
    }
    private void completeRefresh(boolean isAutoUpdate, long started, String string, CompletionHandler handler) {
        WLLogger.logInfo("Earthquakes - " + (isAutoUpdate ? "auto-" : "") + "refreshed (took " + (System.currentTimeMillis()-started) + "ms)");
        if(handler != null) {
            handler.handleString(string);
        }
    }

    private void loadEarthquake(LocalDate startingDate, JSONObject json, ConcurrentHashMap<String, ConcurrentHashMap<String, HashSet<String>>> preEarthquakeDates, ConcurrentHashMap<String, HashSet<PreEarthquake>> territoryEarthquakesMap) {
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

            String place = properties.get("place") instanceof String ? properties.getString("place") : "null";

            final String[] regionValues = getRegionValues(place);
            place = regionValues[0];
            final String country = regionValues[1], subdivision = regionValues[2];

            final String dateString = date.getDateString();
            final PreEarthquake preEarthquake = new PreEarthquake(id, place, country, subdivision, magnitude, location);
            preEarthquakeDates.putIfAbsent(dateString, new ConcurrentHashMap<>());
            preEarthquakeDates.get(dateString).putIfAbsent(magnitude, new HashSet<>());
            preEarthquakeDates.get(dateString).get(magnitude).add(preEarthquake.toString());

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
            final StringBuilder builder = new StringBuilder("{");
            boolean isFirst = true;
            for(PreEarthquake earthquake : territoryEarthquakes) {
                builder.append(isFirst ? "" : ",").append(earthquake.toString());
                isFirst = false;
            }
            builder.append("}");
            final String string = builder.toString();
            recentTerritoryEarthquakes.put(territory, string);
            topRecentTerritoryEarthquakes.put(territory, string);
        }
    }

    private void getEarthquake(String id, CompletionHandler handler) {
        requestJSONObject("https://earthquake.usgs.gov/fdsnws/event/1/query?eventid=" + id + "&format=geojson", RequestMethod.GET, new CompletionHandler() {
            @Override
            public void handleJSONObject(JSONObject json) {
                if(json != null) {
                    final JSONObject properties = json.getJSONObject("properties");
                    final Object mag = properties.get("mag");
                    final String magnitude = mag != null ? mag.toString() : "0";

                    final JSONObject geometry = json.getJSONObject("geometry");
                    final JSONArray coordinates = geometry.getJSONArray("coordinates");
                    final boolean isPoint = geometry.getString("type").equalsIgnoreCase("point");
                    final double latitude = isPoint ? coordinates.getDouble(1) : -1, longitude = isPoint ? coordinates.getDouble(0) : -1;
                    final Location location = new Location(latitude, longitude);

                    final String url = properties.getString("url"), cause = properties.getString("type").toUpperCase();

                    String place = properties.get("place") instanceof String ? properties.getString("place") : "null";
                    final String[] regionValues = getRegionValues(place);
                    place = regionValues[0];
                    final String country = regionValues[1], subdivision = regionValues[2];

                    final long time = properties.getLong("time"), lastUpdated = properties.getLong("updated");
                    final Earthquake earthquake = new Earthquake(country, subdivision, cause, magnitude, place, time, lastUpdated, 0, location, url);
                    final String string = earthquake.toString();
                    handler.handleString(string);
                } else {
                    handler.handleString(null);
                }
            }
        });
    }

    private String[] getRegionValues(String place) {
        String country = null, territory = null;
        final String[] values = place.split(", ");
        final String targetValue = values[values.length-1];
        final String targetValueLowercase = targetValue.toLowerCase();
        final String target = targetValueLowercase.replace(" region", "").replace(" ", "");
        WLCountry wlcountry = WLCountry.valueOfBackendID(target);
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
