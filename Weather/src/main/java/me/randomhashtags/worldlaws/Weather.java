package me.randomhashtags.worldlaws;

import me.randomhashtags.worldlaws.earthquakes.Earthquakes;
import me.randomhashtags.worldlaws.hurricanes.Hurricanes;
import me.randomhashtags.worldlaws.location.CustomCountry;
import me.randomhashtags.worldlaws.weather.WeatherController;
import me.randomhashtags.worldlaws.weather.country.WeatherAU;
import me.randomhashtags.worldlaws.weather.country.WeatherCA;
import me.randomhashtags.worldlaws.weather.country.WeatherUS;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.logging.Level;

public final class Weather implements DataValues {

    private String allAlerts;
    private volatile boolean isFirst;
    private int COMPLETED_HANDLERS;
    private HashSet<WeatherController> autoUpdating;
    private HashMap<String, String> countries;

    public static void main(String[] args) {
        new Weather().init();
    }

    private void init() {
        countries = new HashMap<>();
        autoUpdating = new HashSet<>();
        LocalServer.start("Weather", WL_WEATHER_PORT, new CompletionHandler() {
            @Override
            public void handleClient(WLClient client) {
                final String target = client.getTarget();
                getResponse(target, new CompletionHandler() {
                    @Override
                    public void handle(Object object) {
                        final String string = object.toString();
                        client.sendResponse(string);
                    }
                });
            }
        });
    }
    private WeatherController[] getCountries() {
        return new WeatherController[] {
                WeatherAU.INSTANCE,
                WeatherCA.INSTANCE,
                WeatherUS.INSTANCE,
        };
    }

    private void getResponse(String target, CompletionHandler handler) {
        final String[] values = target.split("/");
        final String key = values[0];
        switch (key) {
            case "alerts":
                getAlertsResponse(target.substring(key.length()+1), handler);
                break;
            case "earthquakes":
                if(values.length == 1) {
                    handler.handle(Earthquakes.INSTANCE.getYears());
                } else {
                    final String[] earthquakeValues = target.substring(key.length()+1).split("/");
                    getEarthquakeResponse(earthquakeValues, handler);
                }
                break;
            case "hurricanes":
                final int year = Integer.parseInt(values[1]);
                Hurricanes.INSTANCE.getAtlanticSeason(year, handler);
                break;
            default:
                break;
        }
    }
    private void getAlertsResponse(String value, CompletionHandler handler) {
        final String[] values = value.split("/");
        switch (values[0]) {
            case "all":
                getAllAlerts(handler);
                break;
            case "event":
                getAllEventAlerts(values[1], handler);
                break;
            default:
                WLUtilities.getCustomCountry(values[0], new CompletionHandler() {
                    @Override
                    public void handleCustomCountry(CustomCountry country) {
                        if(values.length >= 2) {
                            final WeatherController weather = getCountryWeather(country);
                            if(weather != null) {
                                if(values.length == 2) {
                                    weather.getTerritoryAlerts(values[1], handler);
                                } else {
                                    weather.getEventAlerts(values[2], handler);
                                }
                            } else {
                                WLLogger.log(Level.WARNING, "Weather - missing WeatherController for country \"" + country.getBackendID() + "\"!");
                            }
                        } else {
                            getAlertEvents(country, handler);
                        }
                    }
                });
                break;
        }
    }
    private void getEarthquakeResponse(String[] values, CompletionHandler handler) {
        final int year = Integer.parseInt(values[0]);
        switch (values.length) {
            case 1:
                Earthquakes.INSTANCE.getEarthquakeCounts(year, handler);
                break;
            case 2:
                Earthquakes.INSTANCE.getEarthquakes(year, Integer.parseInt(values[1]), handler);
                break;
            case 3:
                Earthquakes.INSTANCE.getEarthquakes(year, Integer.parseInt(values[1]), values[2], handler);
                break;
            default:
                break;
        }
    }

    private void getAlertEvents(CustomCountry country, CompletionHandler handler) {
        final WeatherController weather = getCountryWeather(country);
        getAlertEvents(weather, handler);
    }
    private void getAlertEvents(WeatherController controller, CompletionHandler handler) {
        if(controller != null) {
            if(!autoUpdating.contains(controller)) {
                autoUpdating.add(controller);
                final long TEN_MINUTES = 1000*60*10;
                final String country = controller.getCountryBackendID().getValue();
                final CompletionHandler completion = new CompletionHandler() {
                    @Override
                    public void handle(Object object) {
                        refreshAllAlertEvents(null);
                    }
                };
                controller.startAutoUpdates(TEN_MINUTES, new CompletionHandler() {
                    @Override
                    public void handle(Object object) {
                        countries.put(country, object.toString());
                        handler.handle(object);
                        completion.handle(null);
                    }
                }, new CompletionHandler() {
                    @Override
                    public void handle(Object object) {
                        countries.put(country, object.toString());
                        updateAllAlertsJSON();
                    }
                });
            } else {
                controller.getAlertEvents(handler);
            }
        }
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
    private void getAllEventAlerts(String event, CompletionHandler handler) {
        final StringBuilder builder = new StringBuilder("{");
        final WeatherController[] controllers = getCountries();
        final int max = controllers.length;
        isFirst = true;
        COMPLETED_HANDLERS = 0;
        for(WeatherController controller : controllers) {
            controller.getEventAlerts(event, new CompletionHandler() {
                @Override
                public void handle(Object object) {
                    final String country = controller.getCountryBackendID().getValue();
                    final String source = "\"source\":" + controller.getSource().toString();
                    final String alerts = "\"alerts\":" + object.toString();
                    final String string = "\"" + country + "\":{" + source + "," + alerts + "}";
                    builder.append(isFirst ? "" : ",").append(string);
                    isFirst = false;
                    completedHandler();
                    if(getCompletedHandlers() == max) {
                        handler.handle(builder.append("}").toString());
                    }
                }
            });
        }
    }

    private void getAllAlerts(CompletionHandler handler) {
        if(allAlerts != null) {
            handler.handle(allAlerts);
        } else {
            allAlerts = "[]";
            refreshAllAlertEvents(handler);
        }
    }

    private void refreshAllAlertEvents(CompletionHandler handler) {
        final WeatherController[] countries = getCountries();
        final int max = countries.length;
        COMPLETED_HANDLERS = 0;
        for(WeatherController weather : countries) {
            new Thread(() -> getAlertEvents(weather, new CompletionHandler() {
                @Override
                public void handle(Object object) {
                    completedHandler();
                    if(getCompletedHandlers() == max) {
                        updateAllAlertsJSON();
                        if(handler != null) {
                            handler.handle(allAlerts);
                        }
                    }
                }
            })).start();
        }
    }

    private WeatherController getCountryWeather(CustomCountry country) {
        final String backendID = country.getBackendID();
        for(WeatherController weather : getCountries()) {
            if(backendID.equals(weather.getCountryBackendID().getValue())) {
                return weather;
            }
        }
        return null;
    }

    private synchronized void completedHandler() {
        COMPLETED_HANDLERS += 1;
    }
    private synchronized int getCompletedHandlers() {
        return COMPLETED_HANDLERS;
    }
}
