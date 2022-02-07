package me.randomhashtags.worldlaws.earthquakes;

import me.randomhashtags.worldlaws.WLLogger;
import me.randomhashtags.worldlaws.WLUtilities;
import me.randomhashtags.worldlaws.stream.ParallelStream;
import me.randomhashtags.worldlaws.weather.WeatherController;
import me.randomhashtags.worldlaws.weather.country.WeatherUSA;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

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

    public String getResponse(String value) {
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
    private String getAlertsForCountry(String country, String[] values) {
        final WeatherController weather = getCountryWeather(country);
        if(weather != null) {
            if(values == null) {
                final String countryBackendID = weather.getCountry().getBackendID();
                if(countries.containsKey(countryBackendID)) {
                    return countries.get(countryBackendID);
                } else {
                    final String value = refreshCountry(weather);
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
    private String getAlertsForSubdivision(String country, String subdivision, String[] values) {
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

    private String getAllPreAlerts(String event) {
        final WeatherController[] controllers = getCountries();
        final HashSet<String> values = new HashSet<>();
        new ParallelStream<WeatherController>().stream(Arrays.asList(controllers), controller -> {
            final String string = controller.getPreAlerts(event);
            if(string != null) {
                final String country = controller.getCountry().getBackendID();
                final String source = "\"source\":{" + controller.getSource().toString() + "}";
                final String alerts = "\"alerts\":" + string;
                final String value = "\"" + country + "\":{" + source + "," + alerts + "}";
                values.add(value);
            }
        });

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
        return value;
    }

    private String getAll() {
        if(allAlertsJSON == null) {
            refresh(false);
        }
        return allAlertsJSON;
    }
    private String refreshCountry(WeatherController controller) {
        final String countryBackendID = controller.getCountry().getBackendID();
        final String string = controller.refresh();
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
        new ParallelStream<WeatherController>().stream(Arrays.asList(countries), controller -> {
            refreshCountry(controller);
            controllerLoadTimes.put(controller.getClass().getSimpleName(), System.currentTimeMillis());
        });

        updateJSON();
        final StringBuilder loadTimes = new StringBuilder();
        boolean isFirst = true;
        for(Map.Entry<String, Long> map : controllerLoadTimes.entrySet()) {
            final String simpleName = map.getKey();
            final String time = WLUtilities.getElapsedTime(map.getValue());
            loadTimes.append(isFirst ? "" : ",").append(simpleName).append(" took ").append(time);
            isFirst = false;
        }
        WLLogger.logInfo("WeatherAlerts - " + (isAutoUpdate ? "auto-" : "") + "refreshed (took " + WLUtilities.getElapsedTime(started) + " total, " + loadTimes.toString() + ")");
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
