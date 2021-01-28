package me.randomhashtags.worldlaws.earthquakes;

import me.randomhashtags.worldlaws.*;
import me.randomhashtags.worldlaws.location.Location;
import me.randomhashtags.worldlaws.location.Territories;
import me.randomhashtags.worldlaws.location.Territory;
import org.json.JSONArray;
import org.json.JSONObject;

import java.time.Month;
import java.time.Year;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;

public enum Earthquakes implements RestAPI {
    INSTANCE;

    private final String yearsJSON;
    private final HashMap<Integer, String> years;
    private final HashMap<Integer, HashMap<Month, String>> months;
    private final HashMap<Integer, HashMap<Integer, HashMap<String, String>>> territories;

    Earthquakes() {
        years = new HashMap<>();
        months = new HashMap<>();
        territories = new HashMap<>();
        final Set<Integer> listOfYears = new HashSet<>();
        listOfYears.addAll(Arrays.asList(
                1638,
                1663,
                1727,
                1732,
                1737,
                1744,
                1755,
                1761,
                1769,
                1774,
                1783,
                1791
        ));
        listOfYears.addAll(Arrays.asList(
                1800,
                1804,
                1808,
                1810,
                1811,
                1812,
                1817,
                1823,
                1827,
                1828,
                1833,
                1834,
                1836,
                1838
        ));
        final int year = WLUtilities.getTodayYear();
        for(int i = 1840; i <= year; i++) {
            if(i != 1842 && i != 1846 && i != 1849 && i != 1854) {
                listOfYears.add(i);
            }
        }
        yearsJSON = listOfYears.toString();
    }

    public void getResponse(String target, String key, String[] values, CompletionHandler handler) {
        if(values.length == 1) {
            handler.handle(yearsJSON);
        } else {
            final String[] earthquakeValues = target.substring(key.length()+1).split("/");
            final int year = Integer.parseInt(earthquakeValues[0]);
            switch (earthquakeValues.length) {
                case 1:
                    getEarthquakeCounts(year, handler);
                    break;
                case 2:
                    getEarthquakes(year, Integer.parseInt(earthquakeValues[1]), handler);
                    break;
                case 3:
                    getEarthquakes(year, Integer.parseInt(earthquakeValues[1]), earthquakeValues[2], handler);
                    break;
                default:
                    break;
            }
        }
    }

    private void getEarthquakeCounts(int year, CompletionHandler handler) {
        if(years.containsKey(year)) {
            handler.handle(years.get(year));
        } else {
            refreshEarthquakeCount(year, handler);
        }
    }
    private void getEarthquakes(int year, int monthValue, CompletionHandler handler) {
        final Month month = Month.of(monthValue);
        if(months.containsKey(year) && months.get(year).containsKey(month)) {
            handler.handle(months.get(year).get(month));
        } else {
            refreshEarthquakes(year, month, handler);
        }
    }
    private void getEarthquakes(int year, int monthValue, String territory, CompletionHandler handler) {
        final String value = getEarthquakesForTerritory(year, monthValue, territory);
        if(value != null) {
            handler.handle(value);
        } else {
            final Month month = Month.of(monthValue);
            refreshEarthquakes(year, month, new CompletionHandler() {
                @Override
                public void handle(Object object) {
                    final String targetValue = getEarthquakesForTerritory(year, monthValue, territory), updatedValue = targetValue != null ? targetValue : "[]";
                    handler.handle(updatedValue);
                }
            });
        }
    }
    private String getEarthquakesForTerritory(int year, int monthValue, String territory) {
        return territories.containsKey(year) && territories.get(year).containsKey(monthValue) ? territories.get(year).get(monthValue).getOrDefault(territory, null) : null;
    }

    private void refreshEarthquakeCount(int year, CompletionHandler handler) {
        final long started = System.currentTimeMillis();
        years.put(year, "[]");
        final HashSet<String> jsons = new HashSet<>();
        loadNew(started, year, jsons, handler);
    }
    private void loadNew(long started, int year, HashSet<String> jsons, CompletionHandler handler) {
        final AtomicInteger completed = new AtomicInteger(0);
        Arrays.asList(Month.values()).parallelStream().forEach(month -> refreshEarthquakeCount(year, month, new CompletionHandler() {
            @Override
            public void handle(Object object) {
                final int value = completed.addAndGet(1);
                if(object != null) {
                    jsons.add(object.toString());
                }
                if(value == 12) {
                    final StringBuilder builder = new StringBuilder("[");
                    boolean isFirst = true;
                    for(String json : jsons) {
                        builder.append(isFirst ? "" : ",").append(json);
                        isFirst = false;
                    }
                    builder.append("]");
                    final String string = builder.toString();
                    years.put(year, string);
                    WLLogger.log(Level.INFO, "Earthquakes - refreshed " + year + "'s earthquake count (took " + (System.currentTimeMillis()-started) + "ms)");
                    handler.handle(string);
                }
            }
        }));
    }
    private void refreshEarthquakeCount(int year, Month month, CompletionHandler handler) {
        final int maxDay = month.length(Year.isLeap(year)), monthValue = month.getValue();
        final String url = "https://earthquake.usgs.gov/fdsnws/event/1/count?format=geojson&starttime=" + year + "-" + monthValue + "-01&endtime=" + year + "-" + monthValue + "-" + maxDay;
        requestJSONObject(url, RequestMethod.GET, new CompletionHandler() {
            @Override
            public void handleJSONObject(JSONObject json) {
                final int totalEarthquakes = json.getInt("count");
                if(totalEarthquakes == 0) {
                    handler.handle(null);
                } else {
                    final String monthJSON = new EarthquakeMonth(year, month, totalEarthquakes).toString();
                    if(handler != null) {
                        handler.handle(monthJSON);
                    }
                }
            }
        });
    }

    private void refreshEarthquakes(int year, Month month, CompletionHandler handler) {
        final long started = System.currentTimeMillis();
        final int maxDay = month.length(Year.isLeap(year)), monthValue = month.getValue();
        if(!months.containsKey(year)) {
            months.put(year, new HashMap<>());
        }
        months.get(year).put(month, "[]");
        if(!territories.containsKey(year)) {
            territories.put(year, new HashMap<>());
        }
        final String url = "https://earthquake.usgs.gov/fdsnws/event/1/query?format=geojson&starttime=" + year + "-" + monthValue + "-01&endtime=" + year + "-" + monthValue + "-" + maxDay;
        requestJSONObject(url, RequestMethod.GET, new CompletionHandler() {
            @Override
            public void handleJSONObject(JSONObject jsonobject) {
                final JSONArray array = jsonobject.getJSONArray("features");
                final HashMap<String, HashSet<Earthquake>> earthquakes = new HashMap<>();

                for(Object obj : array) {
                    final JSONObject json = (JSONObject) obj;
                    final JSONObject properties = json.getJSONObject("properties"), geometry = json.getJSONObject("geometry");
                    final String cause = properties.getString("type").toUpperCase();
                    final Object mag = properties.get("mag");
                    final String magnitude = mag != null ? mag.toString() : "0";
                    final String url = properties.getString("url"), place = properties.getString("place");
                    final long time = properties.getLong("time"), lastUpdated = properties.getLong("updated");
                    //final String detailsURL = properties.getString("detail");
                    final JSONArray coordinates = geometry.getJSONArray("coordinates");
                    final boolean isPoint = geometry.getString("type").equalsIgnoreCase("point");
                    final double latitude = isPoint ? coordinates.getDouble(1) : -1, longitude = isPoint ? coordinates.getDouble(0) : -1;
                    final Location location = new Location(latitude, longitude);

                    final Earthquake earthquake = new Earthquake(cause, magnitude, place, time, lastUpdated, 0, location, url);
                    final String territory = earthquake.getTerritory();
                    if(!earthquakes.containsKey(territory)) {
                        earthquakes.put(territory, new HashSet<>());
                    }
                    earthquakes.get(territory).add(earthquake);
                }
                final HashSet<Earthquake> allEarthquakes = new HashSet<>();
                final HashMap<String, String> territoryMap = new HashMap<>();
                for(Map.Entry<String, HashSet<Earthquake>> set : earthquakes.entrySet()) {
                    final String key = set.getKey();
                    final Territory territory = Territories.valueOfTerritoryAbbreviation(key);
                    final String name = (territory != null ? territory.getName() : key).toLowerCase();
                    final HashSet<Earthquake> values = earthquakes.get(key);
                    final String value = getEarthquakeJSONArray(values);
                    territoryMap.put(name.replace(" ", ""), value);
                    allEarthquakes.addAll(values);
                }
                territories.get(year).put(monthValue, territoryMap);
                setupEarthquakeTerritoryJSON(started, year, month, allEarthquakes, handler);
            }
        });
    }
    private void setupEarthquakeTerritoryJSON(long started, int year, Month month, HashSet<Earthquake> earthquakes, CompletionHandler handler) {
        final HashMap<String, Integer> territoryList = new HashMap<>();
        for(Earthquake earthquake : earthquakes) {
            final String territory = earthquake.getTerritory();
            final int previous = territoryList.getOrDefault(territory, 0);
            territoryList.put(territory, previous+1);
        }
        final StringBuilder builder = new StringBuilder("[");
        boolean isFirst = true;
        for(Map.Entry<String, Integer> set : territoryList.entrySet()) {
            final EarthquakeTerritory territory = new EarthquakeTerritory(year, month, set.getKey(), set.getValue());
            builder.append(isFirst ? "" : ",").append(territory.toString());
            isFirst = false;
        }
        builder.append("]");
        final String string = builder.toString();
        months.get(year).put(month, string);
        WLLogger.log(Level.INFO, "Earthquakes - refreshed " + year + "'s " + month.name() + " earthquakes (took " + (System.currentTimeMillis()-started) + "ms)");
        handler.handle(string);
    }
    private String getEarthquakeJSONArray(HashSet<Earthquake> values) {
        final StringBuilder builder = new StringBuilder("[");
        boolean isFirst = true;
        for(Earthquake earthquake : values) {
            builder.append(isFirst ? "" : ",").append(earthquake.toString());
            isFirst = false;
        }
        builder.append("]");
        return builder.toString();
    }
}
