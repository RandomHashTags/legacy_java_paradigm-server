package me.randomhashtags.worldlaws.earthquakes;

import me.randomhashtags.worldlaws.*;
import me.randomhashtags.worldlaws.location.Location;
import me.randomhashtags.worldlaws.location.TerritoryAbbreviations;
import org.apache.logging.log4j.Level;
import org.json.JSONArray;
import org.json.JSONObject;

import java.time.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.StreamSupport;

public enum Earthquakes implements RestAPI {
    INSTANCE;

    private String recentEarthquakes, topRecentEarthquakes;
    private HashMap<String, String> recentTerritoryEarthquakes, topRecentTerritoryEarthquakes;

    public void getResponse(String[] values, CompletionHandler handler) { // TODO: cache data for home response and setup auto update
        final String key = values[1];
        switch (key) {
            case "recent":
                getFromTerritory(true, null, handler);
                break;
            case "top":
                getFromTerritory(false, null, handler);
                break;
            case "id":
                final String id = values[2];
                getEarthquake(id, handler);
                break;
            default:
                final int length = values.length;
                if(length >= 3) {
                    switch (values[2]) {
                        case "recent":
                            getFromTerritory(true, key, handler);
                            break;
                        case "top":
                            getFromTerritory(false, key, handler);
                            break;
                        default:
                            break;
                    }
                }
                break;
        }
    }

    private void getFromTerritory(boolean isRecent, String territory, CompletionHandler handler) {
        final String string = isRecent ? recentEarthquakes : topRecentEarthquakes;
        if(string != null) {
            final String value = getValue(isRecent, territory);
            handler.handleString(value);
        } else {
            setupAutoUpdates();
            final CompletionHandler completionHandler = new CompletionHandler() {
                @Override
                public void handleString(String string) {
                    final String value = getValue(isRecent, territory);
                    handler.handleString(value);
                }
            };
            refresh(isRecent, completionHandler);
        }
    }
    private String getValue(boolean isRecent, String territory) {
        final String target = territory == null ? (isRecent ? recentEarthquakes : topRecentEarthquakes) : (isRecent ? recentTerritoryEarthquakes : topRecentTerritoryEarthquakes).getOrDefault(territory, null);
        return target == null || target.isEmpty() ? null : target;
    }

    private void setupAutoUpdates() {
        final long interval = WLUtilities.WEATHER_EARTHQUAKES_UPDATE_INTERVAL;
        new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                refresh(false, null);
            }
        }, interval, interval);
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

    private void refresh(boolean isRecent, CompletionHandler handler) {
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
                    final HashMap<String, String> americanTerritories = TerritoryAbbreviations.getAmericanTerritories();
                    final ConcurrentHashMap<String, HashSet<PreEarthquake>> territoryEarthquakesMap = new ConcurrentHashMap<>();
                    final ConcurrentHashMap<String, ConcurrentHashMap<String, HashSet<String>>> preEarthquakeDates = new ConcurrentHashMap<>();
                    final AtomicInteger completed = new AtomicInteger(0);
                    final int max = array.length();

                    StreamSupport.stream(array.spliterator(), true).forEach(obj -> {
                        final JSONObject earthquakeJSON = (JSONObject) obj;
                        loadEarthquake(startDate, earthquakeJSON, americanTerritories, preEarthquakeDates, territoryEarthquakesMap);
                        if(completed.addAndGet(1) == max) {
                            topRecentEarthquakes = getEarthquakesJSON(null, preEarthquakeDates);
                            recentEarthquakes = getEarthquakesJSON(recentStartingDate, preEarthquakeDates);
                            loadTerritoryEarthquakes(territoryEarthquakesMap);
                            completeRefresh(started, isRecent ? recentEarthquakes : topRecentEarthquakes, handler);
                        }
                    });
                } else {
                    completeRefresh(started, null, handler);
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
    private void completeRefresh(long started, String string, CompletionHandler handler) {
        WLLogger.log(Level.INFO, "Earthquakes - refreshed recent (took " + (System.currentTimeMillis()-started) + "ms)");
        if(handler != null) {
            handler.handleString(string);
        }
    }

    private void loadEarthquake(LocalDate startingDate, JSONObject json, HashMap<String, String> americanTerritories, ConcurrentHashMap<String, ConcurrentHashMap<String, HashSet<String>>> preEarthquakeDates, ConcurrentHashMap<String, HashSet<PreEarthquake>> territoryEarthquakesMap) {
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

            final String place = properties.get("place") instanceof String ? properties.getString("place") : "null";
            String territory = getTerritory(place, americanTerritories);
            final boolean isAmericaKey = americanTerritories.containsKey(territory), isAmerica = isAmericaKey || americanTerritories.containsValue(territory);

            final String dateString = date.getDateString();
            final PreEarthquake preEarthquake = new PreEarthquake(id, place, magnitude, location);
            preEarthquakeDates.putIfAbsent(dateString, new ConcurrentHashMap<>());
            preEarthquakeDates.get(dateString).putIfAbsent(magnitude, new HashSet<>());
            preEarthquakeDates.get(dateString).get(magnitude).add(preEarthquake.toString());

            territory = territory.toLowerCase().replace(" ", "");
            territoryEarthquakesMap.putIfAbsent(territory, new HashSet<>());
            territoryEarthquakesMap.get(territory).add(preEarthquake);
            if(isAmerica) {
                final String unitedstates = "unitedstates";
                territoryEarthquakesMap.putIfAbsent(unitedstates, new HashSet<>());
                territoryEarthquakesMap.get(unitedstates).add(preEarthquake);
            }
        }
    }
    private void loadTerritoryEarthquakes(ConcurrentHashMap<String, HashSet<PreEarthquake>> territoryEarthquakesMap) {
        for(Map.Entry<String, HashSet<PreEarthquake>> territoryEarthquakeMap : territoryEarthquakesMap.entrySet()) {
            final String territory = territoryEarthquakeMap.getKey();
            final HashSet<PreEarthquake> territoryEarthquakes = territoryEarthquakeMap.getValue();
            final StringBuilder builder = new StringBuilder("[");
            boolean isFirst = true;
            for(PreEarthquake earthquake : territoryEarthquakes) {
                builder.append(isFirst ? "" : ",").append(earthquake.toString());
                isFirst = false;
            }
            builder.append("]");
            recentTerritoryEarthquakes.put(territory, builder.toString());
            topRecentTerritoryEarthquakes.put(territory, builder.toString());
        }
    }

    private void getEarthquake(String id, CompletionHandler handler) {
        requestJSONObject("https://earthquake.usgs.gov/fdsnws/event/1/query?eventid=" + id + "&format=geojson", RequestMethod.GET, new CompletionHandler() {
            @Override
            public void handleJSONObject(JSONObject json) {
                final JSONObject properties = json.getJSONObject("properties");
                final Object mag = properties.get("mag");
                final String magnitude = mag != null ? mag.toString() : "0";

                final JSONObject geometry = json.getJSONObject("geometry");
                final JSONArray coordinates = geometry.getJSONArray("coordinates");
                final boolean isPoint = geometry.getString("type").equalsIgnoreCase("point");
                final double latitude = isPoint ? coordinates.getDouble(1) : -1, longitude = isPoint ? coordinates.getDouble(0) : -1;
                final Location location = new Location(latitude, longitude);

                final String url = properties.getString("url"), cause = properties.getString("type").toUpperCase(), place = properties.getString("place");
                final String territory = getTerritory(place, TerritoryAbbreviations.getAmericanTerritories());
                final long time = properties.getLong("time"), lastUpdated = properties.getLong("updated");
                final Earthquake earthquake = new Earthquake(territory, cause, magnitude, place, time, lastUpdated, 0, location, url);
                final String string = earthquake.toString();
                handler.handleString(string);
            }
        });
    }

    private String getTerritory(String place, HashMap<String, String> americanTerritories) {
        final boolean hasComma = place.contains(", ");
        final String[] values = place.split(hasComma ? ", " : " ");
        final int length = values.length;
        String territory;
        if(hasComma) {
            territory = values[length-1];
        } else {
            final String target = values[length == 1 ? 0 : length-1];
            switch (target.toLowerCase()) {
                case "region":
                    territory = place.split(" region")[0];
                    break;
                case "island":
                    territory = values[length-2] + " " + target;
                    break;
                default:
                    territory = target;
                    break;
            }
        }
        final boolean isAmericaKey = americanTerritories.containsKey(territory), isAmerica = isAmericaKey || americanTerritories.containsValue(territory);
        if(isAmericaKey) {
            territory = americanTerritories.get(territory);
        }
        return territory;
    }
}
