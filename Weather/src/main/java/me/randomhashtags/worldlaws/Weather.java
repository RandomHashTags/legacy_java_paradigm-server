package me.randomhashtags.worldlaws;

import me.randomhashtags.worldlaws.earthquakes.Earthquakes;
import me.randomhashtags.worldlaws.earthquakes.WeatherAlerts;
import me.randomhashtags.worldlaws.tracker.NASA_EONET;
import me.randomhashtags.worldlaws.weather.country.WeatherUSA;

public final class Weather implements WLServer {
    public static void main(String[] args) {
        new Weather().tryLoading();
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
        final WeatherUSA weather = WeatherUSA.INSTANCE;
        final String string = weather.refresh();
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
    public long getHomeResponseUpdateInterval() {
        final APIVersion version = APIVersion.v1;
        final NASA_EONET nasa = NASA_EONET.INSTANCE;
        registerFixedTimer(WLUtilities.WEATHER_ALERTS_UPDATE_INTERVAL, () -> WeatherAlerts.INSTANCE.refresh(true));
        registerFixedTimer(WLUtilities.WEATHER_EARTHQUAKES_UPDATE_INTERVAL, () -> Earthquakes.INSTANCE.refresh(true, false));
        registerFixedTimer(WLUtilities.WEATHER_NASA_WEATHER_EVENT_TRACKER_UPDATE_INTERVAL, () -> nasa.refresh(version));
        registerFixedTimer(WLUtilities.WEATHER_NASA_WEATHER_VOLCANO_UPDATE_INTERVAL, nasa::clearCachedVolcanoes);
        return WLUtilities.WEATHER_HOME_UPDATE_INTERVAL;
    }
}
