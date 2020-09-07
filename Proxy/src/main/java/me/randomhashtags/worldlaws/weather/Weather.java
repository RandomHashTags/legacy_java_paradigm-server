package me.randomhashtags.worldlaws.weather;

import me.randomhashtags.worldlaws.CompletionHandler;
import me.randomhashtags.worldlaws.Jsoupable;
import me.randomhashtags.worldlaws.RestAPI;
import me.randomhashtags.worldlaws.location.Country;
import me.randomhashtags.worldlaws.weather.country.WeatherCA;
import me.randomhashtags.worldlaws.weather.country.WeatherUSA;

import java.util.HashSet;
import java.util.Set;

public enum Weather implements RestAPI, Jsoupable {
    INSTANCE;

    private final Set<CountryWeather> autoUpdating;
    private volatile String allAlerts;
    private volatile boolean isFirst;
    private volatile int completedHandlers;

    Weather() {
        autoUpdating = new HashSet<>();
    }

    public void getAlerts(Country country, CompletionHandler handler) {
        final CountryWeather weather = getCountryWeather(country);
        if(weather != null) {
            if(!autoUpdating.contains(weather)) {
                final long TEN_MINUTES = 1000*60*10;
                final CompletionHandler completion = new CompletionHandler() {
                    @Override
                    public void handle(Object object) {
                        updateAllAlertsJSON(null);
                    }
                };
                weather.startAutoUpdates(TEN_MINUTES, completion);
                autoUpdating.add(weather);
            }
            weather.getAlerts(handler);
        }
    }
    public void getAlerts(Country country, String territory, CompletionHandler handler) {
        final CountryWeather weather = getCountryWeather(country);
        if(weather != null) {
            weather.getAlerts(territory, handler);
        }
    }

    public void getAllAlerts(CompletionHandler handler) {
        if(allAlerts != null) {
            handler.handle(allAlerts);
        } else {
            updateAllAlertsJSON(handler);
        }
    }

    private CountryWeather[] getCountries() {
        return new CountryWeather[] {
                WeatherCA.INSTANCE,
                WeatherUSA.INSTANCE
        };
    }

    private void updateAllAlertsJSON(CompletionHandler handler) {
        final StringBuilder builder = new StringBuilder("{");
        final CountryWeather[] countries = getCountries();
        final int max = countries.length;
        isFirst = true;
        for(CountryWeather weather : countries) {
            new Thread(() -> weather.getAlerts(new CompletionHandler() {
                @Override
                public void handle(Object object) {
                    final String value = (String) object;
                    final String string = "\"" + weather.getCountry().getName() + "\":" + value;
                    builder.append(isFirst ? "" : ",").append(string);
                    isFirst = false;

                    completedHandlers += 1;
                    if(completedHandlers == max) {
                        builder.append("}");
                        final String json = builder.toString();
                        allAlerts = json;
                        if(handler != null) {
                            handler.handle(json);
                        }
                    }
                }
            })).start();
        }
    }

    private CountryWeather getCountryWeather(Country country) {
        for(CountryWeather weather : getCountries()) {
            if(weather.getCountry().equals(country)) {
                return weather;
            }
        }
        return null;
    }
}
