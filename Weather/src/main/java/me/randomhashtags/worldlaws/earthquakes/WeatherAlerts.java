package me.randomhashtags.worldlaws.earthquakes;

import me.randomhashtags.worldlaws.WLLogger;
import me.randomhashtags.worldlaws.WLUtilities;
import me.randomhashtags.worldlaws.locale.JSONObjectTranslatable;
import me.randomhashtags.worldlaws.stream.CompletableFutures;
import me.randomhashtags.worldlaws.weather.WeatherController;
import me.randomhashtags.worldlaws.weather.country.WeatherUSA;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public enum WeatherAlerts {
    INSTANCE;

    private JSONObjectTranslatable allAlertsJSON;
    private final ConcurrentHashMap<String, JSONObjectTranslatable> countries;

    WeatherAlerts() {
        countries = new ConcurrentHashMap<>();
    }

    private WeatherController[] getCountries() {
        return new WeatherController[] {
                WeatherUSA.INSTANCE,
                //WeatherCA.INSTANCE
        };
    }

    public JSONObjectTranslatable getResponse(String value) {
        final String[] values = value.split("/");
        final String key = values[0];
        String country, prefix, target;
        switch (key) {
            case "all":
                return getAll();
            case "event":
                return getAllPreAlerts(values[1]);
            case "country":
                country = values[1];
                prefix = key + "/" + country;
                final String[] countryValues = value.equals(prefix) ? null : value.substring(prefix.length()+1).split("/");
                return getAlertsForCountry(country, countryValues);
            case "subdivision":
                country = values[1];
                final String subdivision = values[2];
                prefix = key + "/" + country + "/" + subdivision;
                target = value.equals(prefix) ? "" : value.substring(prefix.length()+1);
                return getAlertsForSubdivision(country, subdivision, target.split("/"));
            default:
                return null;
        }
    }
    private JSONObjectTranslatable getAlertsForCountry(String country, String[] values) {
        final WeatherController weather = getCountryWeather(country);
        if(weather != null) {
            if(values == null) {
                final String countryBackendID = weather.getCountry().getBackendID();
                if(countries.containsKey(countryBackendID)) {
                    return countries.get(countryBackendID);
                } else {
                    final JSONObjectTranslatable value = refreshCountry(weather);
                    countries.put(countryBackendID, value);
                }
            } else {
                switch (values[0]) {
                    case "event":
                        return weather.getEventPreAlerts().get(values[1]);
                    case "id":
                        return weather.getAlert(values[1]);
                    default:
                        break;
                }
            }
        }
        return null;
    }
    private JSONObjectTranslatable getAlertsForSubdivision(String country, String subdivision, String[] values) {
        final WeatherController weather = getCountryWeather(country);
        if(weather != null) {
            switch (values[0]) {
                case "":
                    return weather.getSubdivisionEvents(subdivision);
                case "event":
                    return weather.getSubdivisionPreAlerts(subdivision, values[1]);
                default:
                    return null;
            }
        } else {
            return null;
        }
    }

    private JSONObjectTranslatable getAllPreAlerts(String event) {
        final WeatherController[] controllers = getCountries();
        final HashMap<String, JSONObjectTranslatable> values = new HashMap<>();
        new CompletableFutures<WeatherController>().stream(Arrays.asList(controllers), controller -> {
            final JSONObjectTranslatable preAlerts = controller.getPreAlerts(event);
            if(preAlerts != null) {
                final String country = controller.getCountry().getBackendID();
                final JSONObjectTranslatable json = new JSONObjectTranslatable();
                json.put("source", controller.getSource().toJSONObject());
                json.put("alerts", preAlerts);
                json.addTranslatedKey("alerts");
                values.put(country, json);
            }
        });

        JSONObjectTranslatable json = null;
        if(!values.isEmpty()) {
            json = new JSONObjectTranslatable();
            for(Map.Entry<String, JSONObjectTranslatable> map : values.entrySet()) {
                final String country = map.getKey();
                json.put(country, map.getValue());
                json.addTranslatedKey(country);
            }
        }
        return json;
    }

    private JSONObjectTranslatable getAll() {
        if(allAlertsJSON == null) {
            refresh(false);
        }
        return allAlertsJSON;
    }
    private JSONObjectTranslatable refreshCountry(WeatherController controller) {
        final String countryBackendID = controller.getCountry().getBackendID();
        final JSONObjectTranslatable string = controller.refresh();
        if(string != null) {
            countries.put(countryBackendID, string);
        } else {
            countries.remove(countryBackendID);
        }
        return string;
    }
    public void refresh(boolean isAutoUpdate) {
        final long started = System.currentTimeMillis();
        final HashMap<String, Long> controllerLoadTimes = new HashMap<>();
        final WeatherController[] countries = getCountries();
        new CompletableFutures<WeatherController>().stream(Arrays.asList(countries), controller -> {
            refreshCountry(controller);
            controllerLoadTimes.put(controller.getClass().getSimpleName(), System.currentTimeMillis()-started);
        });

        updateJSON();
        final StringBuilder loadTimes = new StringBuilder();
        boolean isFirst = true;
        for(Map.Entry<String, Long> map : controllerLoadTimes.entrySet()) {
            final String time = WLUtilities.getElapsedTimeFromMilliseconds(map.getValue());
            loadTimes.append(isFirst ? "" : ",").append(map.getKey()).append(" took ").append(time);
            isFirst = false;
        }
        WLLogger.logInfo("WeatherAlerts - " + (isAutoUpdate ? "auto-" : "") + "refreshed (took " + WLUtilities.getElapsedTime(started) + " total, " + loadTimes.toString() + ")");
    }
    private void updateJSON() {
        final JSONObjectTranslatable json = new JSONObjectTranslatable();
        for(Map.Entry<String, JSONObjectTranslatable> map : countries.entrySet()) {
            final String country = map.getKey();
            json.put(country, map.getValue());
            json.addTranslatedKey(country);
        }
        allAlertsJSON = json;
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
