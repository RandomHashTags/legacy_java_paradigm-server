package me.randomhashtags.worldlaws;

import me.randomhashtags.worldlaws.earthquakes.Earthquakes;
import me.randomhashtags.worldlaws.earthquakes.WeatherAlerts;
import me.randomhashtags.worldlaws.locale.JSONObjectTranslatable;
import me.randomhashtags.worldlaws.request.ServerRequest;
import me.randomhashtags.worldlaws.request.ServerRequestType;
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
        final long started = System.currentTimeMillis();
        final WeatherUSA usa = WeatherUSA.INSTANCE;
        final JSONObjectTranslatable json = usa.refresh();
        WLLogger.logInfo("Weather;test;json=" + (json != null ? json.toString() : "null") + ";took " + WLUtilities.getElapsedTime(started));
    }

    @Override
    public ServerRequestType[] getRequestTypes() {
        return ServerRequestTypeWeather.values();
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
        registerFixedTimer(UpdateIntervals.Weather.ALERTS, () -> WeatherAlerts.INSTANCE.refresh(true));
        registerFixedTimer(UpdateIntervals.Weather.EARTHQUAKES, () -> earthquakes.refresh(true, false));
        registerFixedTimer(UpdateIntervals.Weather.EARTHQUAKES_CLEAR_CACHE, earthquakes::clearCachedEarthquakes);
        registerFixedTimer(UpdateIntervals.Weather.NASA_WEATHER_EVENT_TRACKER, () -> nasa.refresh(version));
        registerFixedTimer(UpdateIntervals.Weather.NASA_WEATHER_VOLCANOS, nasa::clearCachedVolcanoes);
        return UpdateIntervals.Weather.HOME;
    }
}
