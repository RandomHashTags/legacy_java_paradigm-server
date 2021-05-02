package me.randomhashtags.worldlaws;

import me.randomhashtags.worldlaws.observances.Holidays;
import org.apache.logging.log4j.Level;

public final class UpcomingEvents implements DataValues {

    public static void main(String[] args) {
        new UpcomingEvents();
    }

    UpcomingEvents() {
        test();
        //load();
    }

    private void test() {
        Holidays.INSTANCE.getNearHolidays(new CompletionHandler() {
            @Override
            public void handle(Object object) {
                WLLogger.log(Level.INFO, "UpcomingEvents;test;object=" + ((String) object).replace("\n", "\\n"));
            }
        });

        /*UpcomingEventLoader.INSTANCE.getResponse("home", new CompletionHandler() {
            @Override
            public void handle(Object object) {
                WLLogger.log(Level.INFO, "UpcomingEvents;test;object=" + ((String) object).replace("\n", "\\n"));
            }
        });*/
    }

    private void load() {
        final UpcomingEventLoader loader = UpcomingEventLoader.INSTANCE;
        LocalServer.start("UpcomingEvents", WL_UPCOMING_EVENTS_PORT, new CompletionHandler() {
            @Override
            public void handleClient(WLClient client) {
                final String target = client.getTarget();
                loader.getResponse(target, new CompletionHandler() {
                    @Override
                    public void handle(Object object) {
                        final String string = object.toString();
                        client.sendResponse(string);
                    }
                });
            }
        });
    }
}
