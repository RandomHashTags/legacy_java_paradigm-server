package me.randomhashtags.worldlaws.earthquakes;

import me.randomhashtags.worldlaws.CompletionHandler;
import me.randomhashtags.worldlaws.WLLogger;
import me.randomhashtags.worldlaws.weather.WeatherController;
import me.randomhashtags.worldlaws.weather.country.WeatherUSA;
import org.apache.logging.log4j.Level;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public enum WeatherAlerts {
    INSTANCE;

    private String allAlertsJSON;
    private final HashSet<WeatherController> autoUpdating;
    private final HashMap<String, String> countries;

    WeatherAlerts() {
        countries = new HashMap<>();
        autoUpdating = new HashSet<>();
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
        switch (key) {
            case "all":
                getAllAlertEvents(handler);
                break;
            case "event":
                getAllPreAlerts(values[1], handler);
                break;
            default:
                final int length = values.length;
                if(length == 1) {
                    getAlertEvents(key, handler);
                } else {
                    final WeatherController weather = getCountryWeather(key);
                    if(weather != null) {
                        final String countryValue = values[1];
                        switch (countryValue) {
                            case "id":
                                weather.getAlert(values[2], handler);
                                break;
                            case "zone":
                                final String zoneID = value.substring((key + "/zone/").length());
                                weather.getZone(zoneID, handler);
                            case "zones":
                                final int prefix = (key + "/zones/").length();
                                final String[] zoneIDs = value.substring(prefix).split(",");
                                weather.getZones(zoneIDs, handler);
                                break;
                            default:
                                if(length == 2) {
                                    weather.getTerritoryEvents(countryValue, handler);
                                } else {
                                    weather.getTerritoryPreAlerts(countryValue, values[2], handler);
                                }
                                break;
                        }
                    } else {
                        WLLogger.log(Level.WARN, "WeatherAlerts - missing WeatherController for country \"" + key + "\"!");
                    }
                }
                break;
        }
    }

    private void getAllPreAlerts(String event, CompletionHandler handler) {
        final StringBuilder builder = new StringBuilder("{");
        final WeatherController[] controllers = getCountries();
        final int max = controllers.length;
        final AtomicInteger completed = new AtomicInteger(0);
        Arrays.asList(controllers).parallelStream().forEach(controller -> {
            controller.getPreAlerts(event, new CompletionHandler() {
                @Override
                public void handleString(String string) {
                    final String country = controller.getCountry().getBackendID();
                    final String source = "\"source\":{" + controller.getSource().toString() + "}";
                    final String alerts = "\"alerts\":" + string;
                    final String value = "\"" + country + "\":{" + source + "," + alerts + "}";
                    builder.append(completed.get() == 0 ? "" : ",").append(value);
                    if(completed.addAndGet(1) == max) {
                        handler.handleString(builder.append("}").toString());
                    }
                }
            });
        });
    }

    private void getAlertEvents(String countryBackendID, CompletionHandler handler) {
        final WeatherController weather = getCountryWeather(countryBackendID);
        getAlertEvents(weather, handler);
    }
    private void getAlertEvents(WeatherController controller, CompletionHandler handler) {
        if(controller != null) {
            if(!autoUpdating.contains(controller)) {
                autoUpdating.add(controller);
                final String country = controller.getCountry().getBackendID();
                controller.startAutoUpdates(new CompletionHandler() {
                    @Override
                    public void handleString(String string) {
                        countries.put(country, string);
                        handler.handleString(string);
                    }
                }, new CompletionHandler() {
                    @Override
                    public void handleString(String string) {
                        countries.put(country, string);
                        updateAllAlertsJSON();
                    }
                });
            } else {
                controller.getEvents(handler);
            }
        } else {
            handler.handleString(null);
        }
    }

    private void getAllAlertEvents(CompletionHandler handler) {
        if(allAlertsJSON != null) {
            handler.handleString(allAlertsJSON);
        } else {
            allAlertsJSON = "{}";
            refreshAllAlertEvents(handler);
        }
    }
    private void refreshAllAlertEvents(CompletionHandler handler) {
        final long started = System.currentTimeMillis();
        countries.clear();
        final HashMap<String, Long> controllerLoadTimes = new HashMap<>();
        final WeatherController[] countries = getCountries();
        final int max = countries.length;
        final AtomicInteger completed = new AtomicInteger(0);
        Arrays.asList(countries).parallelStream().forEach(weather -> {
            getAlertEvents(weather, new CompletionHandler() {
                @Override
                public void handleString(String string) {
                    controllerLoadTimes.put(weather.getClass().getSimpleName(), System.currentTimeMillis()-started);
                    if(completed.addAndGet(1) == max) {
                        updateAllAlertsJSON();
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
            });
        });
    }
    private void updateAllAlertsJSON() {
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
