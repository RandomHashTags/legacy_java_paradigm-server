package me.randomhashtags.worldlaws;

import me.randomhashtags.worldlaws.event.entertainment.Movies;
import me.randomhashtags.worldlaws.event.entertainment.MusicAlbums;
import me.randomhashtags.worldlaws.event.entertainment.VideoGames;
import me.randomhashtags.worldlaws.event.space.RocketLaunches;
import me.randomhashtags.worldlaws.event.sports.UFC;
import me.randomhashtags.worldlaws.observances.Holidays;
import org.apache.logging.log4j.Level;

import java.time.LocalDate;
import java.time.Month;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public enum UpcomingEventLoader {
    INSTANCE;

    private static final HashSet<EventController> CONTROLLERS = new HashSet<>() {{
        addAll(Arrays.asList(
                Movies.INSTANCE,
                //NASANeo.INSTANCE,
                //NFL.INSTANCE, // problem
                MusicAlbums.INSTANCE,
                RocketLaunches.INSTANCE,
                //SpaceX.INSTANCE,
                UFC.INSTANCE,
                VideoGames.INSTANCE
        ));
    }};

    private String homeJSON, weeklyEvents;
    private final HashMap<String, String> dates;

    UpcomingEventLoader() {
        dates = new HashMap<>();
    }

    private EventController valueOfEventType(String eventType) {
        final Optional<EventController> test = CONTROLLERS.stream().filter(controller -> {
            return eventType.equalsIgnoreCase(controller.getType().name());
        }).findFirst();
        return test.orElse(null);
    }

    public void getResponse(String target, CompletionHandler handler) {
        final String[] values = target.split("/");
        final String key = values[0];
        switch (key) {
            case "home":
                getHomeJSON(handler);
                break;
            case "date":
                getEventsFromStringDate(values[1], handler);
                break;
            case "happeningnow":
                //StreamingEvents.TWITCH.getUpcomingEvents(handler);
                break;
            case "holidays":
                final String value = target.substring(key.length()+1);
                Holidays.INSTANCE.getResponse(value, handler);
                break;
            default:
                final EventController controller = valueOfEventType(key);
                if(controller != null) {
                    controller.getUpcomingEvent(values[1], handler);
                } else {
                    WLLogger.log(Level.ERROR, "UpcomingEventLoader - failed to getResponse for key \"" + key + "\"!");
                }
                break;
        }
    }
    private void getHomeJSON(CompletionHandler handler) {
        if(homeJSON != null) {
            handler.handle(homeJSON);
        } else {
            final long started = System.currentTimeMillis();
            final HashSet<String> list = new HashSet<>() {{
                add("near_holidays");
                add("weekly_events");
            }};
            final int max = list.size();
            final HashSet<String> homeValues = new HashSet<>();
            final AtomicInteger completed = new AtomicInteger(0);
            list.parallelStream().forEach(identifier -> {
                getHomeValue(identifier, new CompletionHandler() {
                    @Override
                    public void handle(Object object) {
                        final int value = completed.addAndGet(1);
                        final String homeValueString = "\"" + identifier + "\":" + object.toString();
                        homeValues.add(homeValueString);
                        if(value == max) {
                            final StringBuilder builder = new StringBuilder("{");
                            boolean isFirst = true;
                            for(String homeValue : homeValues) {
                                builder.append(isFirst ? "" : ",").append(homeValue);
                                isFirst = false;
                            }
                            builder.append("}");
                            final String string = builder.toString();
                            homeJSON = string;
                            WLLogger.log(Level.INFO, "UpcomingEventLoader - loaded homeJSON (took " + (System.currentTimeMillis()-started) + "ms)");
                            handler.handle(string);
                        }
                    }
                });
            });
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

    private void getEventsFromThisWeek(CompletionHandler handler) {
        if(weeklyEvents != null) {
            handler.handle(weeklyEvents);
        } else {
            refreshEventsFromThisWeek(handler);
            setupAutoUpdates();
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
        final long started = System.currentTimeMillis();
        final LocalDate now = LocalDate.now();
        final HashSet<String> dates = new HashSet<>();
        for(int i = 0; i < 7; i++) {
            dates.add(getEventStringForDate(now.plusDays(i)));
        }

        final int max = dates.size();
        final HashSet<String> eventValues = new HashSet<>();
        final AtomicInteger completed = new AtomicInteger(0);
        for(String eventDate : dates) {
            getEventsFromStringDate(eventDate, new CompletionHandler() {
                @Override
                public void handle(Object object) {
                    final int value = completed.addAndGet(1);
                    final String objectString = object.toString();
                    if(!objectString.equals("{}")) {
                        final String eventDateValue = "\"" + eventDate + "\":" + objectString;
                        eventValues.add(eventDateValue);
                    }
                    if(value == max) {
                        final StringBuilder builder = new StringBuilder("{");
                        boolean isFirst = true;
                        for(String eventValue : eventValues) {
                            builder.append(isFirst ? "" : ",").append(eventValue);
                            isFirst = false;
                        }
                        builder.append("}");
                        final String string = builder.toString();
                        weeklyEvents = string;
                        WLLogger.log(Level.INFO, "UpcomingEventLoader - refreshed events from this week (took " + (System.currentTimeMillis()-started) + "ms)");
                        if(handler != null) {
                            handler.handle(string);
                        }
                    }
                }
            });
        }
    }
    private String getEventStringForDate(LocalDate date) {
        return date.getMonthValue() + "-" + date.getYear() + "-" + date.getDayOfMonth();
    }
    private void getEventsFromStringDate(String targetDate, CompletionHandler handler) {
        if(dates.containsKey(targetDate)) {
            handler.handle(dates.get(targetDate));
        } else {
            final String[] valueDates = targetDate.split("-");
            final Month month = Month.of(Integer.parseInt(valueDates[0]));
            final int day = Integer.parseInt(valueDates[2]), year = Integer.parseInt(valueDates[1]);
            getEventsFromDate(new EventDate(month, day, year), new CompletionHandler() {
                @Override
                public void handle(Object object) {
                    final String string = object.toString();
                    dates.put(targetDate, string);
                    handler.handle(string);
                }
            });
        }
    }
    private void getEventsFromDate(EventDate date, CompletionHandler handler) {
        final int max = CONTROLLERS.size();
        final List<String> values = new ArrayList<>();
        final AtomicInteger completed = new AtomicInteger(0);
        for(EventController controller : CONTROLLERS) {
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
        };
    }
}
