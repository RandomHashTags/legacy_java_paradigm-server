package me.randomhashtags.worldlaws.earthquakes;

import me.randomhashtags.worldlaws.CompletionHandler;
import me.randomhashtags.worldlaws.WLLogger;
import me.randomhashtags.worldlaws.weather.recode.NewWeatherController;
import me.randomhashtags.worldlaws.weather.recode.country.WeatherUSA;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;

public enum WeatherAlerts {
    INSTANCE;

    private String allAlerts;
    private final HashSet<NewWeatherController> autoUpdating;
    private final HashMap<String, String> countries;

    WeatherAlerts() {
        countries = new HashMap<>();
        autoUpdating = new HashSet<>();
    }

    private NewWeatherController[] getCountries() {
        return new NewWeatherController[] {
                WeatherUSA.INSTANCE,
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
                    final NewWeatherController weather = getCountryWeather(key);
                    if(weather != null) {
                        final String countryValue = values[1];
                        switch (countryValue) {
                            case "id":
                                weather.getAlert(values[2], handler);
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
                        WLLogger.log(Level.WARNING, "WeatherAlerts - missing WeatherController for country \"" + key + "\"!");
                    }
                }
                break;
        }
    }

    private void getAllPreAlerts(String event, CompletionHandler handler) {
        final StringBuilder builder = new StringBuilder("{");
        final NewWeatherController[] controllers = getCountries();
        final int max = controllers.length;
        final AtomicInteger completedHandlers = new AtomicInteger(0);
        Arrays.asList(controllers).parallelStream().forEach(controller -> {
            controller.getPreAlerts(event, new CompletionHandler() {
                @Override
                public void handle(Object object) {
                    final String country = controller.getCountry().getBackendID();
                    final String source = "\"source\":" + controller.getSource().toString();
                    final String alerts = "\"alerts\":" + object.toString();
                    final String string = "\"" + country + "\":{" + source + "," + alerts + "}";
                    builder.append(completedHandlers.get() == 0 ? "" : ",").append(string);
                    final int completed = completedHandlers.addAndGet(1);
                    if(completed == max) {
                        handler.handle(builder.append("}").toString());
                    }
                }
            });
        });
    }

    private void getAlertEvents(String countryBackendID, CompletionHandler handler) {
        final NewWeatherController weather = getCountryWeather(countryBackendID);
        getAlertEvents(weather, handler);
    }
    private void getAlertEvents(NewWeatherController controller, CompletionHandler handler) {
        if(controller != null) {
            if(!autoUpdating.contains(controller)) {
                autoUpdating.add(controller);
                final long TEN_MINUTES = 1000*60*10;
                final String country = controller.getCountry().getBackendID();
                controller.startAutoUpdates(TEN_MINUTES, new CompletionHandler() {
                    @Override
                    public void handle(Object object) {
                        countries.put(country, object.toString());
                        handler.handle(object);
                    }
                }, new CompletionHandler() {
                    @Override
                    public void handle(Object object) {
                        countries.put(country, object.toString());
                        updateAllAlertsJSON();
                    }
                });
            } else {
                controller.getEvents(handler);
            }
        }
    }

    private void getAllAlertEvents(CompletionHandler handler) {
        if(allAlerts != null) {
            handler.handle(allAlerts);
        } else {
            allAlerts = "{}";
            refreshAllAlertEvents(handler);
        }
    }
    private void refreshAllAlertEvents(CompletionHandler handler) {
        final long started = System.currentTimeMillis();
        final NewWeatherController[] countries = getCountries();
        final int max = countries.length;
        final AtomicInteger completedHandlers = new AtomicInteger(0);
        Arrays.asList(countries).parallelStream().forEach(weather -> {
            getAlertEvents(weather, new CompletionHandler() {
                @Override
                public void handle(Object object) {
                    final int completed = completedHandlers.addAndGet(1);
                    if(completed == max) {
                        updateAllAlertsJSON();
                        WLLogger.log(Level.INFO, "WeatherAlerts - refreshed All Alert Events (took " + (System.currentTimeMillis()-started) + "ms)");
                        if(handler != null) {
                            handler.handle(allAlerts);
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
        allAlerts = builder.toString();
    }

    private NewWeatherController getCountryWeather(String countryBackendID) {
        for(NewWeatherController weather : getCountries()) {
            if(countryBackendID.equals(weather.getCountry().getBackendID())) {
                return weather;
            }
        }
        return null;
    }
}
