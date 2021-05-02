package me.randomhashtags.worldlaws.earthquakes.recode;

import me.randomhashtags.worldlaws.CompletionHandler;
import me.randomhashtags.worldlaws.RequestMethod;
import me.randomhashtags.worldlaws.RestAPI;
import me.randomhashtags.worldlaws.WLLogger;
import me.randomhashtags.worldlaws.earthquakes.Earthquake;
import me.randomhashtags.worldlaws.EventDate;
import me.randomhashtags.worldlaws.location.Location;
import me.randomhashtags.worldlaws.location.TerritoryAbbreviations;
import org.apache.logging.log4j.Level;
import org.json.JSONArray;
import org.json.JSONObject;

import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.TimeUnit;

public enum NewEarthquakes implements RestAPI {
    INSTANCE;

    private String recentEarthquakes, topRecentEarthquakes;
    private HashMap<String, String> recentTerritoryEarthquakes, topRecentTerritoryEarthquakes;

    public void getResponse(String[] values, CompletionHandler handler) {
        final String key = values[1];
        switch (key) {
            case "recent":
                getRecent(null, handler);
                break;
            case "top":
                getRecent(true, null, handler);
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
                            getRecent(key, handler);
                            break;
                        case "top":
                            getTopRecent(key, handler);
                            break;
                        default:
                            break;
                    }
                }
                break;
        }
    }

    private void getRecent(String territory, CompletionHandler handler) {
        getRecent(false, territory, handler);
    }
    private void getTopRecent(String territory, CompletionHandler handler) {
        getRecent(true, territory, handler);
    }
    private void getRecent(boolean isTop, String territory, CompletionHandler handler) {
        final String string = isTop ? topRecentEarthquakes : recentEarthquakes;
        if(string != null) {
            final String value = getValue(isTop, territory);
            handler.handle(value); // TODO: fix this absolute doo doo braindead pepega
        } else {
            setupAutoUpdates();
            final CompletionHandler completionHandler = new CompletionHandler() {
                @Override
                public void handle(Object object) {
                    final String value = getValue(isTop, territory);
                    handler.handle(value);
                }
            };
            if(isTop) {
                refreshTopRecent(completionHandler);
            } else {
                refreshRecent(completionHandler);
            }
        }
    }
    private String getValue(boolean isTop, String territory) {
        return territory == null ? (isTop ? topRecentEarthquakes : recentEarthquakes) : (isTop ? topRecentTerritoryEarthquakes : recentTerritoryEarthquakes).getOrDefault(territory, null);
    }

    private void setupAutoUpdates() {
        final long oneHour = TimeUnit.HOURS.toMillis(1);
        new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                refreshRecent(null);
                refreshTopRecent(null);
            }
        }, oneHour, oneHour);
    }

    private String getURLRequest(int minusDays) {
        final LocalDate endDate = LocalDate.now(), startDate = endDate.minusDays(minusDays);
        final int startDateYear = startDate.getYear();
        final String startDateString = startDateYear + "-" + startDate.getMonthValue() + "-" + startDate.getDayOfMonth();
        final int endDateYear = endDate.getYear();
        final String endDateString = endDateYear + "-" + endDate.getMonthValue() + "-" + endDate.getDayOfMonth();
        return "https://earthquake.usgs.gov/fdsnws/event/1/query?format=geojson&starttime=" + startDateString + "&endtime=" + endDateString;
    }
    private void refreshRecent(CompletionHandler handler) {
        final long started = System.currentTimeMillis();
        final String url = getURLRequest(7);
        requestJSONObject(url, RequestMethod.GET, new CompletionHandler() {
            @Override
            public void handleJSONObject(JSONObject jsonobject) {
                recentTerritoryEarthquakes = new HashMap<>();
                final StringBuilder builder = new StringBuilder("{");
                if(jsonobject != null) {
                    final JSONArray array = jsonobject.getJSONArray("features");
                    final HashMap<String, String> americanTerritories = TerritoryAbbreviations.getAmericanTerritories();
                    final HashMap<String, HashSet<PreEarthquake>> territoryEarthquakesMap = new HashMap<>();
                    final HashMap<String, HashSet<String>> preEarthquakeDates = new HashMap<>();

                    for(Object obj : array) {
                        final JSONObject json = (JSONObject) obj;
                        loadEarthquake(-5, json, americanTerritories, preEarthquakeDates, territoryEarthquakesMap);
                    }

                    boolean isFirst = true;
                    for(Map.Entry<String, HashSet<String>> map : preEarthquakeDates.entrySet()) {
                        final String dateString = map.getKey();
                        final HashSet<String> preEarthquakes = map.getValue();
                        final StringBuilder preEarthquakeBuilder = new StringBuilder("[");
                        boolean isFirstPreEarthquake = true;
                        for(String preEarthquake : preEarthquakes) {
                            preEarthquakeBuilder.append(isFirstPreEarthquake ? "" : ",").append(preEarthquake);
                            isFirstPreEarthquake = false;
                        }
                        preEarthquakeBuilder.append("]");
                        builder.append(isFirst ? "" : ",").append("\"").append(dateString).append("\":").append(preEarthquakeBuilder.toString());
                        isFirst = false;
                    }
                    loadTerritoryEarthquakes(territoryEarthquakesMap, recentTerritoryEarthquakes);
                }
                builder.append("}");
                recentEarthquakes = builder.toString();
                WLLogger.log(Level.INFO, "NewEarthquakes - refreshed recent (" + recentTerritoryEarthquakes.size() + ") (took " + (System.currentTimeMillis()-started) + "ms)");
                if(handler != null) {
                    handler.handle(null);
                }
            }
        });
    }
    private void refreshTopRecent(CompletionHandler handler) {
        final long started = System.currentTimeMillis();
        final String url = getURLRequest(30);
        requestJSONObject(url, RequestMethod.GET, new CompletionHandler() {
            @Override
            public void handleJSONObject(JSONObject jsonobject) {
                topRecentTerritoryEarthquakes = new HashMap<>();
                final StringBuilder builder = new StringBuilder("{");
                if(jsonobject != null) {
                    final JSONArray array = jsonobject.getJSONArray("features");
                    final HashMap<String, String> americanTerritories = TerritoryAbbreviations.getAmericanTerritories();
                    final HashMap<String, HashSet<PreEarthquake>> territoryEarthquakesMap = new HashMap<>();
                    final HashMap<String, HashSet<String>> preEarthquakeDates = new HashMap<>();

                    for(Object obj : array) {
                        final JSONObject json = (JSONObject) obj;
                        loadEarthquake(3, json, americanTerritories, preEarthquakeDates, territoryEarthquakesMap);
                    }

                    boolean isFirst = true;
                    for(Map.Entry<String, HashSet<String>> map : preEarthquakeDates.entrySet()) {
                        final String dateString = map.getKey();
                        final HashSet<String> preEarthquakes = map.getValue();
                        final StringBuilder preEarthquakeBuilder = new StringBuilder("{");
                        boolean isFirstPreEarthquake = true;
                        for(String preEarthquake : preEarthquakes) {
                            preEarthquakeBuilder.append(isFirstPreEarthquake ? "" : ",").append(preEarthquake);
                            isFirstPreEarthquake = false;
                        }
                        preEarthquakeBuilder.append("}");
                        builder.append(isFirst ? "" : ",").append("\"").append(dateString).append("\":").append(preEarthquakeBuilder);
                        isFirst = false;
                    }
                    loadTerritoryEarthquakes(territoryEarthquakesMap, topRecentTerritoryEarthquakes);
                }
                builder.append("}");
                topRecentEarthquakes = builder.toString();
                WLLogger.log(Level.INFO, "NewEarthquakes - refreshed top recent (" + topRecentTerritoryEarthquakes.size() + ") (took " + (System.currentTimeMillis()-started) + "ms)");
                if(handler != null) {
                    handler.handle(null);
                }
            }
        });
    }

    private void loadEarthquake(float minimumMagnitude, JSONObject json, HashMap<String, String> americanTerritories, HashMap<String, HashSet<String>> preEarthquakeDates, HashMap<String, HashSet<PreEarthquake>> territoryEarthquakesMap) {
        final JSONObject properties = json.getJSONObject("properties");
        final Object mag = properties.get("mag");
        final String id = json.getString("id"), magnitude = mag != null && !mag.toString().equals("null") ? mag.toString() : "0.00";
        if(Float.parseFloat(magnitude) >= minimumMagnitude) {
            final JSONObject geometry = json.getJSONObject("geometry");
            final JSONArray coordinates = geometry.getJSONArray("coordinates");
            final boolean isPoint = geometry.getString("type").equalsIgnoreCase("point");
            final double latitude = isPoint ? coordinates.getDouble(1) : -1, longitude = isPoint ? coordinates.getDouble(0) : -1;
            final Location location = new Location(latitude, longitude);

            final String place = properties.getString("place");
            final boolean hasComma = place.contains(", ");
            final String[] values = place.split(hasComma ? ", " : " ");
            final int length = values.length;
            String territory;
            if(hasComma) {
                territory = values[length-1];
            } else {
                final boolean isOne = length == 1;
                final String target = values[isOne ? 0 : length-1];
                territory = isOne || target.equals("Sea") || target.equals("Peninsula") ? target : target.equals("region") ? place.split(" region")[0] : target.equalsIgnoreCase("island") ? values[length-2] + " " + target : target;
            }

            final boolean isAmericaKey = americanTerritories.containsKey(territory), isAmerica = isAmericaKey || americanTerritories.containsValue(territory);
            if(isAmericaKey) {
                territory = americanTerritories.get(territory);
            }

            final long time = properties.getLong("time");
            final EventDate date = new EventDate(time);
            final String dateString = date.getMonth().getValue() + "-" + date.getYear() + "-" + date.getDay();
            final PreEarthquake preEarthquake = new PreEarthquake(id, place, magnitude, location);
            if(!preEarthquakeDates.containsKey(dateString)) {
                preEarthquakeDates.put(dateString, new HashSet<>());
            }
            preEarthquakeDates.get(dateString).add(preEarthquake.toString());

            territory = territory.toLowerCase().replace(" ", "");
            if(!territoryEarthquakesMap.containsKey(territory)) {
                territoryEarthquakesMap.put(territory, new HashSet<>());
            }
            territoryEarthquakesMap.get(territory).add(preEarthquake);
            if(isAmerica) {
                final String unitedstates = "unitedstates";
                if(!territoryEarthquakesMap.containsKey(unitedstates)) {
                    territoryEarthquakesMap.put(unitedstates, new HashSet<>());
                }
                territoryEarthquakesMap.get(unitedstates).add(preEarthquake);
            }
        }
    }
    private void loadTerritoryEarthquakes(HashMap<String, HashSet<PreEarthquake>> territoryEarthquakesMap, HashMap<String, String> territoryEarthquakesHashMap) {
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
            territoryEarthquakesHashMap.put(territory, builder.toString());
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
                final boolean hasComma = place.contains(", ");
                final String[] values = place.split(hasComma ? ", " : " ");
                final int length = values.length;
                String territory;
                if(hasComma) {
                    territory = values[length-1];
                } else {
                    final boolean isOne = length == 1;
                    final String target = values[isOne ? 0 : length-1];
                    territory = isOne || target.equals("Sea") || target.equals("Peninsula") ? target : target.equals("region") ? place.split(" region")[0] : target.equalsIgnoreCase("island") ? values[length-2] + " " + target : target;
                }

                final HashMap<String, String> americanTerritories = TerritoryAbbreviations.getAmericanTerritories();
                final boolean isAmericaKey = americanTerritories.containsKey(territory), isAmerica = isAmericaKey || americanTerritories.containsValue(territory);
                if(isAmericaKey) {
                    territory = americanTerritories.get(territory);
                }

                final long time = properties.getLong("time"), lastUpdated = properties.getLong("updated");
                final Earthquake earthquake = new Earthquake(territory, cause, magnitude, place, time, lastUpdated, 0, location, url);
                final String string = earthquake.toString();
                handler.handle(string);
            }
        });
    }
}
