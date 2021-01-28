package me.randomhashtags.worldlaws;

import me.randomhashtags.worldlaws.event.EventDate;
import me.randomhashtags.worldlaws.event.entertainment.Movies;
import me.randomhashtags.worldlaws.event.entertainment.MusicAlbums;
import me.randomhashtags.worldlaws.event.entertainment.VideoGames;
import me.randomhashtags.worldlaws.event.sports.UFC;
import me.randomhashtags.worldlaws.happeningnow.StreamingEvents;
import me.randomhashtags.worldlaws.location.WLCountry;
import me.randomhashtags.worldlaws.location.CustomCountry;

import java.time.LocalDate;
import java.time.Month;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.stream.Stream;

public final class UpcomingEvents implements DataValues {

    private static final EventController[] CONTROLLERS = new EventController[] {
            Movies.INSTANCE,
            //NASANeo.INSTANCE,
            //NFL.INSTANCE, // problem
            MusicAlbums.INSTANCE,
            //RocketLaunches.INSTANCE,
            //SpaceX.INSTANCE,
            UFC.INSTANCE,
            VideoGames.INSTANCE,
    };

    private String eventsJSON;
    private HashMap<CustomCountry, String> countries;
    private HashMap<EventController, String> jsons;
    private HashMap<String, String> dates;

    public static void main(String[] args) {
        new UpcomingEvents();
    }

    UpcomingEvents() {
        //test();
        load();
    }

    private void test() {
        final long started = System.currentTimeMillis();
        final Month month = Month.JANUARY;
        final int day = 20, year = 2021;
        final EventDate targetDate = new EventDate(month, day, year);
        getEventsFromDate(targetDate, new CompletionHandler() {
            @Override
            public void handle(Object object) {
                final String string = object.toString();
                WLLogger.log(Level.INFO, "UpcomingEvents;test;" + string + ";(took " + (System.currentTimeMillis()-started) + "ms)");
            }
        });
    }

    private void load() {
        countries = new HashMap<>();
        jsons = new HashMap<>();
        dates = new HashMap<>();

        LocalServer.start("UpcomingEvents", WL_UPCOMING_EVENTS_PORT, new CompletionHandler() {
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

    private EventController valueOfIdentifier(String backendID) {
        for(EventController controller : CONTROLLERS) {
            if(backendID.equalsIgnoreCase(controller.getType().name())) {
                return controller;
            }
        }
        return null;
    }
    private String getListJSON() {
        if(eventsJSON == null) {
            final StringBuilder builder = new StringBuilder("[");
            boolean isFirst = true;
            for(EventController controller : CONTROLLERS) {
                final String identifier = controller.getType().name().toLowerCase();
                builder.append(isFirst ? "" : ",").append("\"").append(identifier).append("\"");
                isFirst = false;
            }
            builder.append("]");
            eventsJSON = builder.toString();
        }
        return eventsJSON;
    }

    private void getResponse(String target, CompletionHandler handler) {
        final String[] values = target.split("/");
        final String key = values[0];
        switch (key) {
            case "home":
                final HashSet<String> list = new HashSet<>() {{
                    add("near_holidays");
                    add("today_events");
                }};
                final int max = list.size();
                final StringBuilder builder = new StringBuilder("{");
                final AtomicInteger completed = new AtomicInteger(0);
                for(String identifier : list) {
                    getHomeValue(identifier, new CompletionHandler() {
                        @Override
                        public void handle(Object object) {
                            final int value = completed.addAndGet(1);
                            builder.append(value == 1 ? "" : ",").append("\"").append(identifier).append("\":").append(object);
                            if(value == max) {
                                builder.append("}");
                                handler.handle(builder.toString());
                            }
                        }
                    });
                }
                break;
            case "date":
                getEventsFromStringDate(values[1], handler);
                break;
            case "happeningnow":
                StreamingEvents.TWITCH.getUpcomingEvents(handler);
                break;
            case "list":
                handler.handle(getListJSON());
                break;
            case "holidays":
                Holidays.INSTANCE.getResponse(values[1], handler);
                break;
            default:
                final EventController controller = valueOfIdentifier(key);
                if(controller != null) {
                    handler.handle(controller.getUpcomingEvent(values[1]));
                }
                break;
        }
    }
    private void getHomeValue(String identifier, CompletionHandler handler) {
        switch (identifier) {
            case "near_holidays":
                Holidays.INSTANCE.getNearHolidays(handler);
                break;
            case "today_events":
                final LocalDate now = LocalDate.now();
                final Month month = now.getMonth();
                final int day = now.getDayOfMonth(), year = now.getYear();
                final String dateString = month.getValue() + "-" + day + "-" + year;
                getEventsFromStringDate(dateString, handler);
                break;
            default:
                break;
        }
    }

    private void getEvents(CustomCountry country, CompletionHandler handler) {
        if(countries.containsKey(country)) {
            handler.handle(countries.get(country));
        } else {
            final List<EventController> events = getEventsFromCountry(country);
            final int max = events.size();
            final AtomicInteger completed = new AtomicInteger(0);
            final StringBuilder builder = new StringBuilder("[");
            events.parallelStream().forEach(controller -> {
                controller.getUpcomingEvents(new CompletionHandler() {
                    @Override
                    public void handle(Object object) {
                        final int value = completed.addAndGet(1);
                        final String identifier = controller.getType().name().toLowerCase(), json = (String) object;
                        final String string = "\"" + identifier + "\":" + json;
                        builder.append(value == 1 ? "" : ",").append(string);

                        if(value == max) {
                            builder.append("]");
                            final String builderValue = builder.toString();
                            countries.put(country, builderValue);
                            handler.handle(builderValue);
                        }
                    }
                });
            });
        }
    }
    public void getEvents(EventController controller, CompletionHandler handler) {
        if(jsons.containsKey(controller)) {
            handler.handle(jsons.get(controller));
        } else {
            refreshEvents(controller, handler);
        }
    }

    private void getEventsFromStringDate(String targetDate, CompletionHandler handler) {
        if(dates.containsKey(targetDate)) {
            handler.handle(dates.get(targetDate));
        } else {
            final long started = System.currentTimeMillis();
            final String[] valueDates = targetDate.split("-");
            final Month month = Month.of(Integer.parseInt(valueDates[0]));
            final int day = Integer.parseInt(valueDates[1]), year = Integer.parseInt(valueDates[2]);
            getEventsFromDate(new EventDate(month, day, year), new CompletionHandler() {
                @Override
                public void handle(Object object) {
                    final String string = object.toString();
                    dates.put(targetDate, string);
                    WLLogger.log(Level.INFO, "UpcomingEvents - loaded date \"" + targetDate + "\" (took " + (System.currentTimeMillis()-started) + "ms)");
                    handler.handle(string);
                }
            });
        }
    }
    private void getEventsFromDate(EventDate date, CompletionHandler handler) {
        final int max = CONTROLLERS.length;
        final Stream<EventController> controllers = Arrays.asList(CONTROLLERS).parallelStream();
        final List<String> values = new ArrayList<>();
        controllers.forEach(controller -> {
            controller.getEventsFromDate(date, new CompletionHandler() {
                @Override
                public void handle(Object object) {
                    final String value = "\"" + controller.getType().name().toLowerCase() + "\":" + object.toString();
                    values.add(value);
                    if(values.size() == max) {
                        final StringBuilder builder = new StringBuilder("{");
                        boolean isFirst = true;
                        for(String string : values) {
                            builder.append(isFirst ? "" : ",").append(string);
                            isFirst = false;
                        }
                        final String string = builder.append("}").toString();
                        handler.handle(string);
                    }
                }
            });
        });
    }

    private void refreshEvents(EventController controller, CompletionHandler handler) {
        controller.getUpcomingEvents(new CompletionHandler() {
            @Override
            public void handle(Object object) {
                final String json = (String) object;
                jsons.put(controller, json);
                handler.handle(json);
            }
        });
    }

    public List<EventController> getEventsFromCountry(@Nullable CustomCountry country) {
        final List<EventController> set = Arrays.asList(CONTROLLERS);
        if(country != null) {
            set.removeIf(event -> {
                final WLCountry backendID  = event.getCountry();
                return backendID == null || !backendID.getBackendID().equalsIgnoreCase(country.getBackendID());
            });
        }
        return set;
    }
}
