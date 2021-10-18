package me.randomhashtags.worldlaws.earthquakes;

import me.randomhashtags.worldlaws.CompletionHandler;
import me.randomhashtags.worldlaws.WLLogger;
import me.randomhashtags.worldlaws.WLUtilities;
import me.randomhashtags.worldlaws.Weather;
import me.randomhashtags.worldlaws.weather.WeatherController;
import me.randomhashtags.worldlaws.weather.country.WeatherUSA;
import org.apache.logging.log4j.Level;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public enum WeatherAlerts {
    INSTANCE;

    private String allAlertsJSON;
    private final ConcurrentHashMap<String, String> countries;

    WeatherAlerts() {
        countries = new ConcurrentHashMap<>();
    }

    private WeatherController[] getCountries() {
        return new WeatherController[] {
                WeatherUSA.INSTANCE,
                //WeatherCA.INSTANCE
        };
    }

    public void getResponse(String value, CompletionHandler handler) {
        final String[] values = value.split("/");
        final String key = values[0];
        String country = null;
        switch (key) {
            case "all":
                getAll(handler);
                break;
            case "event":
                getAllPreAlerts(values[1], handler);
                break;
            case "country":
                country = values[1];
                getAlertsForCountry(country, value.substring(key.length()+country.length()+2).split("/"), handler);
                break;
            case "subdivision":
                country = values[1];
                final String subdivision = values[2];
                final String prefix = key + "/" + country + "/" + subdivision;
                final String target = value.equals(prefix) ? "" : value.substring(prefix.length()+1);
                WLLogger.log(Level.INFO, "WeatherAlerts;getResponse;subdivision;target=" + target);
                getAlertsForSubdivision(country, subdivision, target.split("/"), handler);
                break;
            default:
                break;
        }
    }
    private void getAlertsForCountry(String country, String[] values, CompletionHandler handler) {
        final WeatherController weather = getCountryWeather(country);
        if(weather != null) {
            switch (values[0]) {
                case "event":
                    weather.getEventPreAlerts().get(values[1]);
                    break;
                case "id":
                    weather.getAlert(values[1], handler);
                    break;
                case "zone":
                    weather.getZone(values[1], handler);
                    break;
                case "zones":
                    final String[] zoneIDs = values[1].split(",");
                    weather.getZones(zoneIDs, handler);
                    break;
                default:
                    handler.handleString(null);
                    break;
            }
        } else {
            handler.handleString(null);
        }
    }
    private void getAlertsForSubdivision(String country, String subdivision, String[] values, CompletionHandler handler) {
        final WeatherController weather = getCountryWeather(country);
        if(weather != null) {
            switch (values[0]) {
                case "":
                    weather.getSubdivisionEvents(subdivision, handler);
                    break;
                case "event":
                    weather.getSubdivisionPreAlerts(subdivision, values[1], handler);
                    break;
                default:
                    handler.handleString(null);
                    break;
            }
        } else {
            handler.handleString(null);
        }
    }

    private void getAllPreAlerts(String event, CompletionHandler handler) {
        final WeatherController[] controllers = getCountries();
        final HashSet<String> values = new HashSet<>();
        final int max = controllers.length;
        final AtomicInteger completed = new AtomicInteger(0);
        Arrays.asList(controllers).parallelStream().forEach(controller -> controller.getPreAlerts(event, new CompletionHandler() {
            @Override
            public void handleString(String string) {
                if(string != null) {
                    final String country = controller.getCountry().getBackendID();
                    final String source = "\"source\":{" + controller.getSource().toString() + "}";
                    final String alerts = "\"alerts\":" + string;
                    final String value = "\"" + country + "\":{" + source + "," + alerts + "}";
                    values.add(value);
                }
                if(completed.addAndGet(1) == max) {
                    String value = null;
                    if(!values.isEmpty()) {
                        final StringBuilder builder = new StringBuilder("{");
                        boolean isFirst = true;
                        for(String valueString : values) {
                            builder.append(isFirst ? "" : ",").append(valueString);
                            isFirst = false;
                        }
                        builder.append("}");
                        value = builder.toString();
                    }
                    handler.handleString(value);
                }
            }
        }));
    }

    private void getAll(CompletionHandler handler) {
        if(allAlertsJSON != null) {
            handler.handleString(allAlertsJSON);
        } else {
            Weather.INSTANCE.registerFixedTimer(WLUtilities.WEATHER_ALERTS_UPDATE_INTERVAL, new CompletionHandler() {
                @Override
                public void handleObject(Object object) {
                    refresh(null);
                }
            });
            refresh(handler);
        }
    }
    private void getFrom(WeatherController controller, CompletionHandler handler) {
        if(controller != null) {
            final String country = controller.getCountry().getBackendID();
            controller.refresh(new CompletionHandler() {
                @Override
                public void handleString(String string) {
                    countries.put(country, string);
                    handler.handleString(string);
                }
            });
        } else {
            handler.handleString(null);
        }
    }
    private void refresh(CompletionHandler handler) {
        final long started = System.currentTimeMillis();
        final HashMap<String, Long> controllerLoadTimes = new HashMap<>();
        final WeatherController[] countries = getCountries();
        final int max = countries.length;
        final AtomicInteger completed = new AtomicInteger(0);
        Arrays.asList(countries).parallelStream().forEach(weather -> getFrom(weather, new CompletionHandler() {
            @Override
            public void handleString(String string) {
                controllerLoadTimes.put(weather.getClass().getSimpleName(), System.currentTimeMillis()-started);
                if(completed.addAndGet(1) == max) {
                    updateJSON();
                    final StringBuilder loadTimes = new StringBuilder();
                    boolean isFirst = true;
                    for(Map.Entry<String, Long> map : controllerLoadTimes.entrySet()) {
                        final String simpleName = map.getKey();
                        final long time = map.getValue();
                        loadTimes.append(isFirst ? "" : ",").append(simpleName).append(" took ").append(time).append("ms");
                        isFirst = false;
                    }
                    WLLogger.log(Level.INFO, "WeatherAlerts - refreshed All Alert Events (took " + (System.currentTimeMillis()-started) + "ms total, " + loadTimes.toString() + ")");
                    if(handler != null) {
                        handler.handleString(allAlertsJSON);
                    }
                }
            }
        }));
    }
    private void updateJSON() {
        final StringBuilder builder = new StringBuilder("{");
        boolean isFirst = true;
        for(Map.Entry<String, String> map : countries.entrySet()) {
            final String country = map.getKey(), value = map.getValue();
            builder.append(isFirst ? "" : ",").append("\"").append(country).append("\":").append(value);
            isFirst = false;
        }
        builder.append("}");
        allAlertsJSON = builder.toString();
    }

    private WeatherController getCountryWeather(String countryBackendID) {
        for(WeatherController weather : getCountries()) {
            if(countryBackendID.equals(weather.getCountry().getBackendID())) {
                return weather;
            }
        }
        return null;
    }
}
