package me.randomhashtags.worldlaws.weather;

import me.randomhashtags.worldlaws.CompletionHandler;
import me.randomhashtags.worldlaws.Jsoupable;
import me.randomhashtags.worldlaws.NotNull;
import me.randomhashtags.worldlaws.RestAPI;
import me.randomhashtags.worldlaws.event.EventSource;
import me.randomhashtags.worldlaws.location.Country;

import java.util.Timer;
import java.util.TimerTask;

public interface CountryWeather extends RestAPI, Jsoupable {
    Country getCountry();
    EventSource getSourceURL();
    void getAlerts(@NotNull CompletionHandler handler);
    void getAlerts(@NotNull String territory, @NotNull CompletionHandler handler);
    default void startAutoUpdates(long interval, CompletionHandler handler) {
        new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                updateAlerts(handler);
            }
        }, interval, interval);
    }
    void updateAlerts(@NotNull CompletionHandler handler);
}
