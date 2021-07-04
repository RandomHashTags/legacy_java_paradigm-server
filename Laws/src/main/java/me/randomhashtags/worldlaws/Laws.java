package me.randomhashtags.worldlaws;

import me.randomhashtags.worldlaws.country.LawController;
import me.randomhashtags.worldlaws.country.usa.USLaws;
import org.apache.logging.log4j.Level;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public final class Laws implements WLServer {

    private HashMap<String, LawController> countries;

    private final LawController[] CONTROLLERS = new LawController[] {
            USLaws.INSTANCE
    };

    public static void main(String[] args) {
        new Laws();
    }

    private Laws() {
        //test();
        load();
    }

    @Override
    public TargetServer getServer() {
        return TargetServer.LAWS;
    }

    private void test() {
        getRecentActivity(APIVersion.v1, new CompletionHandler() {
            @Override
            public void handleString(String string) {
                WLLogger.log(Level.INFO, "Laws;test;object=" + string);
            }
        });
    }

    @Override
    public void load() {
        countries = new HashMap<>();
        for(LawController country : CONTROLLERS) {
            countries.put(country.getCountry().getBackendID(), country);
        }
        startServer();
    }

    @Override
    public void getServerResponse(APIVersion version, String target, CompletionHandler handler) {
        final String[] values = target.split("/");
        final String key = values[0];
        switch (key) {
            case "recent_activity":
                if(values.length >= 2) {
                    final LawController controller = valueOfCountry(values[1]);
                    if(controller != null) {
                        controller.getRecentActivity(version, handler);
                    }
                } else {
                    getRecentActivity(version, handler);
                }
                break;
            default:
                countries.get(key).getResponse(version, target.substring(key.length()+1), handler);
                break;
        }
    }

    @Override
    public String[] getHomeRequests() {
        return new String[] {
                "recent_activity"
        };
    }

    private void getRecentActivity(APIVersion version, CompletionHandler handler) {
        final long started = System.currentTimeMillis();
        final int max = CONTROLLERS.length;
        final HashMap<String, String> values = new HashMap<>();
        final AtomicInteger completed = new AtomicInteger(0);
        Arrays.asList(CONTROLLERS).parallelStream().forEach(controller -> {
            controller.getRecentActivity(version, new CompletionHandler() {
                @Override
                public void handleString(String string) {
                    if(string != null) {
                        values.put(controller.getCountry().getBackendID(), string);
                    }
                    WLLogger.log(Level.INFO, "Laws - loaded " + controller.getClass().getSimpleName() + "'s recent activity (took " + (System.currentTimeMillis()-started) + "ms)");
                    if(completed.addAndGet(1) == max) {
                        String value = null;
                        if(!values.isEmpty()) {
                            final StringBuilder builder = new StringBuilder("{");
                            boolean isFirst = true;
                            for(Map.Entry<String, String> map : values.entrySet()) {
                                builder.append(isFirst ? "" : ",").append("\"").append(map.getKey()).append("\":").append(map.getValue());
                                isFirst = false;
                            }
                            builder.append("}");
                            value = builder.toString();
                        }
                        WLLogger.log(Level.INFO, "Laws - loaded recent activity (took " + (System.currentTimeMillis()-started) + "ms)");
                        handler.handleString(value);
                    }
                }
            });
        });
    }

    private LawController valueOfCountry(String countryBackendID) {
        for(LawController controller : CONTROLLERS) {
            if(controller.getCountry().getBackendID().equals(countryBackendID)) {
                return controller;
            }
        }
        return null;
    }
}
