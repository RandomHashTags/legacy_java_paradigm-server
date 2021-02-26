package me.randomhashtags.worldlaws;

import me.randomhashtags.worldlaws.earthquakes.WeatherAlerts;
import me.randomhashtags.worldlaws.earthquakes.recode.NewEarthquakes;
import me.randomhashtags.worldlaws.hurricanes.Hurricanes;
import me.randomhashtags.worldlaws.weather.country.WeatherUS;
import org.apache.logging.log4j.Level;

import java.util.HashSet;
import java.util.concurrent.atomic.AtomicInteger;

public final class Weather implements DataValues {

    public static void main(String[] args) {
        new Weather().init();
    }

    private void init() {
        //test();
        load();
    }

    private void test() {
        WeatherUS.INSTANCE.getAlertEvents(new CompletionHandler() {
            @Override
            public void handle(Object object) {
                WLLogger.log(Level.INFO, "Weather;test;WeatherUS.getAlertEvents;object=" + object);
            }
        });
        /*WeatherAlerts.INSTANCE.getResponse("all", new CompletionHandler() {
            @Override
            public void handle(Object object) {
                WLLogger.log(Level.INFO, "Weather;test;WeatherAlerts.getResponse(all);object=" + object.toString());
            }
        });*/
    }
    private void load() {
        LocalServer.start("Weather", WL_WEATHER_PORT, new CompletionHandler() {
            @Override
            public void handleClient(WLClient client) {
                final String target = client.getTarget();
                getResponse(target, new CompletionHandler() {
                    @Override
                    public void handle(Object object) {
                        final String string = object.toString();
                        client.sendResponse(string);
                    }
                });
            }
        });
    }

    private void getResponse(String target, CompletionHandler handler) {
        final String[] values = target.split("/");
        final String key = values[0];
        switch (key) {
            case "home":
                getHomeResponse(handler);
                break;
            case "alerts":
                WeatherAlerts.INSTANCE.getResponse(target.substring(key.length()+1), handler);
                break;
            case "earthquakes":
                NewEarthquakes.INSTANCE.getResponse(values, handler);
                break;
            case "hurricanes":
                final int year = Integer.parseInt(values[1]);
                Hurricanes.INSTANCE.getAtlanticSeason(year, handler);
                break;
            default:
                break;
        }
    }

    private void getHomeResponse(CompletionHandler handler) {
        final HashSet<String> requests = new HashSet<>() {{
            add("alerts/all");
            add("earthquakes/recent");
        }};
        final int max = requests.size();
        final StringBuilder builder = new StringBuilder("{");
        final AtomicInteger completed = new AtomicInteger(0);
        requests.parallelStream().forEach(request -> {
            final String key = request.split("/")[0];
            getResponse(request, new CompletionHandler() {
                @Override
                public void handle(Object object) {
                    builder.append(completed.get() == 0 ? "" : ",").append("\"").append(key).append("\":").append(object.toString());
                    final int value = completed.addAndGet(1);
                    if(value == max) {
                        builder.append("}");
                        handler.handle(builder.toString());
                    }
                }
            });
        });
    }
}
