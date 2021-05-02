package me.randomhashtags.worldlaws;

import me.randomhashtags.worldlaws.earthquakes.WeatherAlerts;
import me.randomhashtags.worldlaws.earthquakes.recode.NewEarthquakes;
import me.randomhashtags.worldlaws.hurricanes.Hurricanes;
import me.randomhashtags.worldlaws.weather.country.WeatherUSA;
import org.apache.logging.log4j.Level;

import java.util.HashSet;
import java.util.concurrent.atomic.AtomicInteger;

public final class Weather implements DataValues {

    public static void main(String[] args) {
        new Weather().init();
    }

    private void init() {
        test();
        //loadServer();
    }

    private void test() {
        WeatherUSA.INSTANCE.refresh(new CompletionHandler() {
            @Override
            public void handle(Object object) {
                loadServer();
            }
        });
    }
    private void loadServer() {
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
        final AtomicInteger completed = new AtomicInteger(0);
        final HashSet<String> values = new HashSet<>();
        requests.parallelStream().forEach(request -> {
            final String key = request.split("/")[0];
            getResponse(request, new CompletionHandler() {
                @Override
                public void handle(Object object) {
                    final String string = "\"" + key + "\":" + object.toString();
                    values.add(string);
                    final int completedHandlers = completed.addAndGet(1);
                    if(completedHandlers == max) {
                        final StringBuilder builder = new StringBuilder("{");
                        boolean isFirst = true;
                        for(String value : values) {
                            builder.append(isFirst ? "" : ",").append(value);
                            isFirst = false;
                        }
                        builder.append("}");
                        handler.handle(builder.toString());
                    }
                }
            });
        });
    }
}
