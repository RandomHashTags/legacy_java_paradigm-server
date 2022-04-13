package me.randomhashtags.worldlaws;

import me.randomhashtags.worldlaws.earthquakes.Earthquakes;
import me.randomhashtags.worldlaws.earthquakes.WeatherAlerts;
import me.randomhashtags.worldlaws.locale.JSONObjectTranslatable;
import me.randomhashtags.worldlaws.request.ServerRequest;
import me.randomhashtags.worldlaws.request.ServerRequestType;
import me.randomhashtags.worldlaws.tracker.NASA_EONET;
import org.json.JSONObject;

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
        final String key = "key", value = "This is ā \ntest!", fixed = LocalServer.fixEscapeValues(value);
        WLLogger.logInfo("Weather;test;value=" + value + ";fixed=" + fixed);
        final String targetJSON = "{\"" + key + "\":\"" + LocalServer.fixEscapeValues(value) + "\"}";
        final JSONObject json1 = new JSONObject(targetJSON);
        final JSONObject json2 = new JSONObject();
        json2.put(key, fixed);
        WLLogger.logInfo("Weather;test;json1=" + json1.toString());
        WLLogger.logInfo("Weather;test;json2=" + json2.toString());
        WLLogger.logInfo("Weather;test;took=" + WLUtilities.getElapsedTime(started));
    }

    @Override
    public JSONObjectTranslatable getServerResponse(APIVersion version, String identifier, ServerRequest request) {
        return null;
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
