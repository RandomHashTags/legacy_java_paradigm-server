package me.randomhashtags.worldlaws;

import me.randomhashtags.worldlaws.earthquakes.Earthquakes;
import me.randomhashtags.worldlaws.earthquakes.WeatherAlerts;
import me.randomhashtags.worldlaws.request.ServerRequest;
import me.randomhashtags.worldlaws.request.server.ServerRequestTypeWeather;
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
    public String getServerResponse(APIVersion version, String identifier, ServerRequest request) {
        final ServerRequestTypeWeather type = (ServerRequestTypeWeather) request.getType();
        final String target = request.getTarget();
        switch (type) {
            case ALERTS:
                return WeatherAlerts.INSTANCE.getResponse(target);
            case EARTHQUAKES:
                return Earthquakes.INSTANCE.getResponse(target.split("/"));
            case NATURAL_EVENTS:
                return NASA_EONET.INSTANCE.getCurrent(version);
            default:
                return null;
        }
    }

    @Override
    public ServerRequest[] getHomeRequests() {
        return new ServerRequest[] {
                new ServerRequest(ServerRequestTypeWeather.ALERTS, "all"),
                new ServerRequest(ServerRequestTypeWeather.EARTHQUAKES, "recent"),
                new ServerRequest(ServerRequestTypeWeather.NATURAL_EVENTS),
        };
    }

    @Override
    public long getHomeResponseUpdateInterval() {
        final APIVersion version = APIVersion.v1;
        final Earthquakes earthquakes = Earthquakes.INSTANCE;
        final NASA_EONET nasa = NASA_EONET.INSTANCE;
        registerFixedTimer(WLUtilities.WEATHER_ALERTS_UPDATE_INTERVAL, () -> WeatherAlerts.INSTANCE.refresh(true));
        registerFixedTimer(WLUtilities.WEATHER_EARTHQUAKES_UPDATE_INTERVAL, () -> earthquakes.refresh(true, false));
        registerFixedTimer(WLUtilities.WEATHER_EARTHQUAKES_CLEAR_CACHE_INTERVAL, earthquakes::clearCachedEarthquakes);
        registerFixedTimer(WLUtilities.WEATHER_NASA_WEATHER_EVENT_TRACKER_UPDATE_INTERVAL, () -> nasa.refresh(version));
        registerFixedTimer(WLUtilities.WEATHER_NASA_WEATHER_VOLCANO_UPDATE_INTERVAL, nasa::clearCachedVolcanoes);
        return WLUtilities.WEATHER_HOME_UPDATE_INTERVAL;
    }
}
