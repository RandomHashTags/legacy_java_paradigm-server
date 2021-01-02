package me.randomhashtags.worldlaws.earthquakes;

import me.randomhashtags.worldlaws.CompletionHandler;
import me.randomhashtags.worldlaws.RequestMethod;
import me.randomhashtags.worldlaws.RestAPI;
import me.randomhashtags.worldlaws.WLLogger;
import me.randomhashtags.worldlaws.location.Territories;
import me.randomhashtags.worldlaws.location.Territory;
import org.json.JSONArray;
import org.json.JSONObject;

import java.time.LocalDate;
import java.time.Month;
import java.time.Year;
import java.util.*;
import java.util.logging.Level;

public enum Earthquakes implements RestAPI {
    INSTANCE;

    private final String yearsJSON;
    private static final HashMap<Integer, String> YEARS = new HashMap<>();
    private static final HashMap<Integer, HashMap<Month, String>> MONTHS = new HashMap<>();
    private static final HashMap<Integer, HashMap<Integer, HashMap<String, String>>> TERRITORIES = new HashMap<>();
    private int COMPLETED_HANDLERS;

    Earthquakes() {
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
        final int year = LocalDate.now().getYear();
        for(int i = 1840; i <= year; i++) {
            if(i != 1842 && i != 1846 && i != 1849 && i != 1854) {
                listOfYears.add(i);
            }
        }
        yearsJSON = listOfYears.toString();
    }

    public String getYears() {
        return yearsJSON;
    }
    public void getEarthquakeCounts(int year, CompletionHandler handler) {
        if(YEARS.containsKey(year)) {
            handler.handle(YEARS.get(year));
        } else {
            refreshEarthquakeCount(year, handler);
        }
    }
    public void getEarthquakes(int year, int monthValue, CompletionHandler handler) {
        final Month month = Month.of(monthValue);
        if(MONTHS.containsKey(year) && MONTHS.get(year).containsKey(month)) {
            handler.handle(MONTHS.get(year).get(month));
        } else {
            refreshEarthquakes(year, month, handler);
        }
    }
    public void getEarthquakes(int year, int monthValue, String territory, CompletionHandler handler) {
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
        return TERRITORIES.containsKey(year) && TERRITORIES.get(year).containsKey(monthValue) ? TERRITORIES.get(year).get(monthValue).getOrDefault(territory, null) : null;
    }

    private void refreshEarthquakeCount(int year, CompletionHandler handler) {
        final long started = System.currentTimeMillis();
        YEARS.put(year, "[]");
        COMPLETED_HANDLERS = 0;
        final HashSet<String> jsons = new HashSet<>();
        for(Month month : Month.values()) {
            new Thread(() -> refreshEarthquakeCount(year, month, new CompletionHandler() {
                @Override
                public void handle(Object object) {
                    if(object != null) {
                        jsons.add(object.toString());
                    }
                    completedHandler();
                    if(getCompletedHandlers() == 12) {
                        final StringBuilder builder = new StringBuilder("[");
                        boolean isFirst = true;
                        for(String json : jsons) {
                            builder.append(isFirst ? "" : ",").append(json);
                            isFirst = false;
                        }
                        builder.append("]");
                        final String string = builder.toString();
                        YEARS.put(year, string);
                        WLLogger.log(Level.INFO, "Earthquakes - refreshed " + year + "'s earthquake count (took " + (System.currentTimeMillis()-started) + "ms)");
                        handler.handle(string);
                    }
                }
            })).start();
        }
    }
    private synchronized void completedHandler() {
        COMPLETED_HANDLERS += 1;
    }
    private synchronized int getCompletedHandlers() {
        return COMPLETED_HANDLERS;
    }
    private void refreshEarthquakeCount(int year, Month month, CompletionHandler handler) {
        final int maxDay = month.length(Year.isLeap(year)), monthValue = month.getValue();
        final String url = "https://earthquake.usgs.gov/fdsnws/event/1/count?format=geojson&starttime=" + year + "-" + monthValue + "-01&endtime=" + year + "-" + monthValue + "-" + maxDay;
        requestJSON(url, RequestMethod.GET, new CompletionHandler() {
            @Override
            public void handle(Object object) {
                final JSONObject json = new JSONObject(object.toString());
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
        if(!MONTHS.containsKey(year)) {
            MONTHS.put(year, new HashMap<>());
        }
        MONTHS.get(year).put(month, "[]");
        if(!TERRITORIES.containsKey(year)) {
            TERRITORIES.put(year, new HashMap<>());
        }
        final String url = "https://earthquake.usgs.gov/fdsnws/event/1/query?format=geojson&starttime=" + year + "-" + monthValue + "-01&endtime=" + year + "-" + monthValue + "-" + maxDay;
        requestJSON(url, RequestMethod.GET, new CompletionHandler() {
            @Override
            public void handle(Object object) {
                final JSONObject jsonobject = new JSONObject(object.toString());
                final JSONArray array = jsonobject.getJSONArray("features");
                final HashMap<String, HashSet<Earthquake>> earthquakes = new HashMap<>();
                for(Object obj : array) {
                    final JSONObject json = (JSONObject) obj;
                    final JSONObject properties = json.getJSONObject("properties");
                    final String cause = properties.getString("type").toUpperCase();
                    final Object mag = properties.get("mag");
                    final String magnitude = mag != null ? mag.toString() : "0";
                    final String url = properties.getString("url"), place = properties.getString("place");
                    final long time = properties.getLong("time"), lastUpdated = properties.getLong("updated");
                    //final String detailsURL = properties.getString("detail");

                    final Earthquake earthquake = new Earthquake(cause, magnitude, place, time, lastUpdated, 0, url);
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
                TERRITORIES.get(year).put(monthValue, territoryMap);
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
        MONTHS.get(year).put(month, string);
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
