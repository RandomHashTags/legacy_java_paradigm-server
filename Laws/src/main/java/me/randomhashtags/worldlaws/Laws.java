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
        USLaws.INSTANCE.getRecentActivity(new CompletionHandler() {
            @Override
            public void handle(Object object) {
                WLLogger.log(Level.INFO, "Laws;test;object=" + object);
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
            case "recently_passed":
                getRecentlyPassed(handler);
                break;
            default:
                countries.get(key).getResponse(target.substring(key.length()+1), handler);
                break;
        }
    }

    @Override
    public String[] getHomeRequests() {
        return new String[] {
                "recently_passed"
        };
    }

    private void getRecentlyPassed(CompletionHandler handler) {
        final long started = System.currentTimeMillis();
        final int max = CONTROLLERS.length;
        final HashMap<String, String> values = new HashMap<>();
        final AtomicInteger completed = new AtomicInteger(0);
        Arrays.asList(CONTROLLERS).parallelStream().forEach(controller -> {
            controller.getRecentActivity(new CompletionHandler() {
                @Override
                public void handle(Object object) {
                    if(object != null) {
                        values.put(controller.getCountry().getBackendID(), object.toString());
                    }
                    if(completed.addAndGet(1) == max) {
                        String string = null;
                        if(!values.isEmpty()) {
                            final StringBuilder builder = new StringBuilder("{");
                            boolean isFirst = true;
                            for(Map.Entry<String, String> map : values.entrySet()) {
                                final String country = map.getKey();
                                final String json = map.getValue();
                                builder.append(isFirst ? "" : ",").append("\"").append(country).append("\":").append(json);
                                isFirst = false;
                            }
                            builder.append("}");
                            string = builder.toString();
                        }
                        WLLogger.log(Level.INFO, "Laws - loaded recently passed (took " + (System.currentTimeMillis()-started) + "ms)");
                        handler.handle(string);
                    }
                }
            });
        });
    }
}
