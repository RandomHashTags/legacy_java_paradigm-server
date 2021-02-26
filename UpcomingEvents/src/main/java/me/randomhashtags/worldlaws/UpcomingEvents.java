package me.randomhashtags.worldlaws;

import me.randomhashtags.worldlaws.event.EventDate;
import me.randomhashtags.worldlaws.event.entertainment.Movies;
import me.randomhashtags.worldlaws.event.entertainment.VideoGames;
import me.randomhashtags.worldlaws.event.space.RocketLaunches;
import me.randomhashtags.worldlaws.event.sports.UFC;
import me.randomhashtags.worldlaws.happeningnow.StreamingEvents;
import me.randomhashtags.worldlaws.location.WLCountry;
import org.apache.logging.log4j.Level;

import java.time.LocalDate;
import java.time.Month;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public final class UpcomingEvents implements DataValues {

    private static final List<EventController> CONTROLLERS = Arrays.asList(
            Movies.INSTANCE,
            //NASANeo.INSTANCE,
            //NFL.INSTANCE, // problem
            //MusicAlbums.INSTANCE,
            RocketLaunches.INSTANCE,
            //SpaceX.INSTANCE,
            UFC.INSTANCE,
            VideoGames.INSTANCE
    );

    private String eventsJSON, weeklyEvents;
    private HashMap<String, String> countries;
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
        final Month month = Month.FEBRUARY;
        final int day = 2, year = 2021;
        final EventDate targetDate = new EventDate(month, day, year);
        RocketLaunches.INSTANCE.getEventsFromDate(targetDate, new CompletionHandler() {
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
                    add("weekly_events");
                }};
                final int max = list.size();
                final StringBuilder builder = new StringBuilder("{");
                final AtomicInteger completed = new AtomicInteger(0);
                list.parallelStream().forEach(identifier -> {
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
                });
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
            case "weekly_events":
                getEventsFromThisWeek(handler);
                break;
            default:
                break;
        }
    }

    private void getEvents(String country, CompletionHandler handler) {
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

    private void getEventsFromThisWeek(CompletionHandler handler) {
        if(weeklyEvents != null) {
            handler.handle(weeklyEvents);
        } else {
            setupAutoUpdates();
            refreshEventsFromThisWeek(handler);
        }
    }
    private void setupAutoUpdates() {
        final long threeHours = TimeUnit.HOURS.toMillis(3);
        new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                dates.clear();
                refreshEventsFromThisWeek(null);
            }
        }, threeHours, threeHours);
    }
    private void refreshEventsFromThisWeek(CompletionHandler handler) {
        final LocalDate now = LocalDate.now();
        final StringBuilder builder = new StringBuilder("{");
        final HashSet<String> dates = new HashSet<>() {{
            add(getEventStringForDate(now));
            add(getEventStringForDate(now.plusDays(1)));
            add(getEventStringForDate(now.plusDays(2)));
            add(getEventStringForDate(now.plusDays(3)));
            add(getEventStringForDate(now.plusDays(4)));
            add(getEventStringForDate(now.plusDays(5)));
            add(getEventStringForDate(now.plusDays(6)));
        }};

        final int max = dates.size();
        final AtomicInteger completed = new AtomicInteger(0);
        for(String identifier : dates) {
            final String eventDate = identifier.substring("events-".length());
            getEventsFromStringDate(eventDate, new CompletionHandler() {
                @Override
                public void handle(Object object) {
                    final int value = completed.addAndGet(1);
                    final String id = "\"" + identifier + "\":" + object.toString();
                    builder.append(value == 1 ? "" : ",").append(id);
                    if(value == max) {
                        builder.append("}");
                        final String string = builder.toString();
                        weeklyEvents = string;
                        if(handler != null) {
                            handler.handle(string);
                        }
                    }
                }
            });
        }
    }
    private String getEventStringForDate(LocalDate date) {
        return "events-" + date.getMonthValue() + "-" + date.getYear() + "-" + date.getDayOfMonth();
    }
    private void getEventsFromStringDate(String targetDate, CompletionHandler handler) {
        if(dates.containsKey(targetDate)) {
            handler.handle(dates.get(targetDate));
        } else {
            final long started = System.currentTimeMillis();
            final String[] valueDates = targetDate.split("-");
            final Month month = Month.of(Integer.parseInt(valueDates[0]));
            final int day = Integer.parseInt(valueDates[2]), year = Integer.parseInt(valueDates[1]);
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
        final int max = CONTROLLERS.size();
        final List<String> values = new ArrayList<>();
        final AtomicInteger completed = new AtomicInteger(0);
        CONTROLLERS.parallelStream().forEach(controller -> {
            controller.getEventsFromDate(date, new CompletionHandler() {
                @Override
                public void handle(Object object) {
                    final int completedValue = completed.addAndGet(1);
                    final String objectString = object.toString();
                    final boolean isEmpty = objectString.equals("[]");
                    if(!isEmpty) {
                        final String value = "\"" + controller.getType().name().toLowerCase() + "\":" + objectString;
                        values.add(value);
                    }
                    if(completedValue == max) {
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

    public List<EventController> getEventsFromCountry(String countryBackendID) {
        final List<EventController> set = new ArrayList<>(CONTROLLERS);
        if(countryBackendID != null) {
            set.removeIf(event -> {
                final WLCountry backendID  = event.getCountry();
                return backendID == null || !backendID.getBackendID().equalsIgnoreCase(countryBackendID);
            });
        }
        return set;
    }
}
