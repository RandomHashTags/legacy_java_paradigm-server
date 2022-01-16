package me.randomhashtags.worldlaws;

import me.randomhashtags.worldlaws.earthquakes.Earthquakes;
import me.randomhashtags.worldlaws.earthquakes.WeatherAlerts;
import me.randomhashtags.worldlaws.tracker.NASA_EONET;
import me.randomhashtags.worldlaws.weather.country.WeatherUSA;

public final class Weather implements WLServer {
    public static final Weather INSTANCE = new Weather();

    public static void main(String[] args) {
        INSTANCE.tryLoading();
    }

    private void tryLoading() {
        //test();
        load();
    }

    @Override
    public TargetServer getServer() {
        return TargetServer.WEATHER;
    }

    private void test() {
        final String string = WeatherUSA.INSTANCE.refresh();
        WLLogger.logInfo("Weather;test;string=" + string);
    }

    @Override
    public String getServerResponse(APIVersion version, String target) {
        final String[] values = target.split("/");
        final String key = values[0];
        switch (key) {
            case "alerts":
                return WeatherAlerts.INSTANCE.getResponse(target.substring(key.length()+1));
            case "earthquakes":
                return Earthquakes.INSTANCE.getResponse(target.substring(key.length()+1).split("/"));
            case "natural_events":
                return NASA_EONET.INSTANCE.getCurrent(version);
            default:
                return null;
        }
    }

    @Override
    public String[] getHomeRequests() {
        return new String[] {
                "alerts/all",
                "earthquakes/recent",
                "natural_events"
        };
    }

    @Override
    public AutoUpdateSettings getAutoUpdateSettings() {
        final long first = WLUtilities.WEATHER_ALERTS_UPDATE_INTERVAL, second = WLUtilities.WEATHER_EARTHQUAKES_UPDATE_INTERVAL;
        final long interval = Math.min(first, second);
        return new AutoUpdateSettings(interval, null);
    }
}
