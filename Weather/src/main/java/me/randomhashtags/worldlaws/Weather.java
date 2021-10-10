package me.randomhashtags.worldlaws;

import me.randomhashtags.worldlaws.earthquakes.Earthquakes;
import me.randomhashtags.worldlaws.earthquakes.WeatherAlerts;
import me.randomhashtags.worldlaws.tracker.NASA_EONET;
import me.randomhashtags.worldlaws.weather.country.WeatherUSA;
import org.apache.logging.log4j.Level;

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
        WeatherUSA.INSTANCE.refresh(new CompletionHandler() {
            @Override
            public void handleString(String string) {
                WeatherAlerts.INSTANCE.getResponse("country/unitedstates/id/urn:oid:2.49.0.1.840.0.d117a720e5b050ef5a350005de5206b7404ce645.001.1", new CompletionHandler() {
                    @Override
                    public void handleString(String string) {
                        WLLogger.log(Level.INFO, "Weather;test;string=" + string);
                    }
                });
            }
        });
    }

    @Override
    public void getServerResponse(APIVersion version, String target, CompletionHandler handler) {
        final String[] values = target.split("/");
        final String key = values[0];
        switch (key) {
            case "alerts":
                WeatherAlerts.INSTANCE.getResponse(target.substring(key.length()+1), handler);
                break;
            case "earthquakes":
                Earthquakes.INSTANCE.getResponse(target.substring(key.length()+1).split("/"), handler);
                break;
            case "natural_events":
                NASA_EONET.INSTANCE.getCurrent(version, handler);
                break;
            default:
                break;
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
