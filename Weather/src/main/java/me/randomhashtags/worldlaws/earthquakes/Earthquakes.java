package me.randomhashtags.worldlaws.earthquakes;

import me.randomhashtags.worldlaws.*;
import me.randomhashtags.worldlaws.location.Location;
import me.randomhashtags.worldlaws.location.TerritoryAbbreviations;
import org.apache.logging.log4j.Level;
import org.json.JSONArray;
import org.json.JSONObject;

import java.time.Clock;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.StreamSupport;

public enum Earthquakes implements RestAPI {
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

    public void getRecent(String territory, CompletionHandler handler) {
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
        final long interval = WLUtilities.WEATHER_ALERTS_UPDATE_INTERVAL;
        new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                refreshRecent(null);
                refreshTopRecent(null);
            }
        }, interval, interval);
    }

    private String getURLRequest(int minusDays) {
        final LocalDate endDate = LocalDate.now(Clock.systemUTC()), startDate = endDate.minusDays(minusDays);
        final int startDateYear = startDate.getYear();
        final String startDateString = startDateYear + "-" + startDate.getMonthValue() + "-" + startDate.getDayOfMonth();
        final int endDateYear = endDate.getYear();
        final String endDateString = endDateYear + "-" + endDate.getMonthValue() + "-" + endDate.getDayOfMonth();
        return "https://earthquake.usgs.gov/fdsnws/event/1/query?format=geojson&starttime=" + startDateString + "&endtime=" + endDateString;
    }
    private void refreshRecent(CompletionHandler handler) {
        refresh(true, handler);
    }
    private void refreshTopRecent(CompletionHandler handler) {
        refresh(false, handler);
    }
    private void refresh(boolean isRecent, CompletionHandler handler) {
        final long started = System.currentTimeMillis();
        final int minusDays = isRecent ? 7 : 30;
        final String url = getURLRequest(minusDays);

        requestJSONObject(url, RequestMethod.GET, new CompletionHandler() {
            @Override
            public void handleJSONObject(JSONObject jsonobject) {
                if(isRecent) {
                    recentTerritoryEarthquakes = new HashMap<>();
                } else {
                    topRecentTerritoryEarthquakes = new HashMap<>();
                }
                final HashMap<String, String> territoryEarthquakes = isRecent ? recentTerritoryEarthquakes : topRecentTerritoryEarthquakes;

                final AtomicInteger count = new AtomicInteger(0);
                final StringBuilder builder = new StringBuilder("{");
                if(jsonobject != null) {
                    final JSONArray array = jsonobject.getJSONArray("features");
                    final HashMap<String, String> americanTerritories = TerritoryAbbreviations.getAmericanTerritories();
                    final ConcurrentHashMap<String, HashSet<PreEarthquake>> territoryEarthquakesMap = new ConcurrentHashMap<>();
                    final ConcurrentHashMap<String, ConcurrentHashMap<String, HashSet<String>>> preEarthquakeDates = new ConcurrentHashMap<>();
                    final AtomicInteger completed = new AtomicInteger(0);
                    final int max = array.length();

                    StreamSupport.stream(array.spliterator(), true).forEach(obj -> {
                        final JSONObject json = (JSONObject) obj;
                        loadEarthquake(json, americanTerritories, preEarthquakeDates, territoryEarthquakesMap);
                        if(completed.addAndGet(1) == max) {
                            boolean isFirst = true;
                            for(Map.Entry<String, ConcurrentHashMap<String, HashSet<String>>> map : preEarthquakeDates.entrySet()) {
                                final String dateString = map.getKey();
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
                            loadTerritoryEarthquakes(territoryEarthquakesMap, territoryEarthquakes);
                            completeRefresh(started, isRecent, builder, count, handler);
                        }
                    });
                } else {
                    completeRefresh(started, isRecent, builder, count, handler);
                }
            }
        });
    }
    private void completeRefresh(long started, boolean isRecent, StringBuilder builder, AtomicInteger count, CompletionHandler handler) {
        builder.append("}");
        final String string = builder.toString();
        if(isRecent) {
            recentEarthquakes = string;
        } else {
            topRecentEarthquakes = string;
        }
        WLLogger.log(Level.INFO, "Earthquakes - refreshed " + (isRecent ? "" : "top ") + "recent (" + count.get() + ") (took " + (System.currentTimeMillis()-started) + "ms)");
        if(handler != null) {
            handler.handle(null);
        }
    }

    private void loadEarthquake(JSONObject json, HashMap<String, String> americanTerritories, ConcurrentHashMap<String, ConcurrentHashMap<String, HashSet<String>>> preEarthquakeDates, ConcurrentHashMap<String, HashSet<PreEarthquake>> territoryEarthquakesMap) {
        final JSONObject properties = json.getJSONObject("properties");
        final Object mag = properties.has("mag") ? properties.get("mag") : null;
        final String magnitude = mag != null && !mag.toString().equals("null") ? mag.toString() : "0.00";
        if(Float.parseFloat(magnitude) >= 2.00) {
            final String id = json.getString("id");
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
    private void loadTerritoryEarthquakes(ConcurrentHashMap<String, HashSet<PreEarthquake>> territoryEarthquakesMap, HashMap<String, String> territoryEarthquakesHashMap) {
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
