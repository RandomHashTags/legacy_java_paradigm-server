package me.randomhashtags.worldlaws;

import me.randomhashtags.worldlaws.event.EventDate;
import me.randomhashtags.worldlaws.event.entertainment.Movies;
import me.randomhashtags.worldlaws.event.entertainment.MusicAlbums;
import me.randomhashtags.worldlaws.event.entertainment.VideoGames;
import me.randomhashtags.worldlaws.event.space.RocketLaunches;
import me.randomhashtags.worldlaws.event.space.SpaceX;
import me.randomhashtags.worldlaws.event.space.nasa.NASANeo;
import me.randomhashtags.worldlaws.event.sports.UFC;
import me.randomhashtags.worldlaws.happeningnow.StreamingEvents;
import me.randomhashtags.worldlaws.holiday.Holidays;
import me.randomhashtags.worldlaws.location.CountryBackendID;
import me.randomhashtags.worldlaws.location.CustomCountry;

import java.time.Month;
import java.util.*;
import java.util.logging.Level;

public final class UpcomingEvents implements DataValues {

    private static final EventController[] CONTROLLERS = new EventController[] {
            Movies.INSTANCE,
            //NASANeo.INSTANCE,
            //NFL.INSTANCE, // problem
            MusicAlbums.INSTANCE,
            //RocketLaunches.INSTANCE,
            SpaceX.INSTANCE,
            UFC.INSTANCE,
            VideoGames.INSTANCE,
    };

    private String eventsJSON;
    private boolean isFirst;
    private final HashMap<CustomCountry, String> countries;
    private final HashMap<EventController, String> jsons;
    private final HashMap<String, String> dates;

    private final HashMap<EventDate, Integer> COMPLETED_HANDLERS;

    public static void main(String[] args) {
        new UpcomingEvents();
    }

    private UpcomingEvents() {
        countries = new HashMap<>();
        jsons = new HashMap<>();
        dates = new HashMap<>();
        COMPLETED_HANDLERS = new HashMap<>();

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
            case "date":
                final String targetDate = values[1];
                if(dates.containsKey(targetDate)) {
                    handler.handle(dates.get(targetDate));
                } else {
                    final long started = System.currentTimeMillis();
                    final String[] valueDates = targetDate.split("-");
                    final Month month = Month.of(Integer.parseInt(valueDates[0]));
                    final int day = Integer.parseInt(valueDates[1]), year = Integer.parseInt(valueDates[2]);
                    getEventsFrom(new EventDate(month, day, year), new CompletionHandler() {
                        @Override
                        public void handle(Object object) {
                            final String string = object.toString();
                            dates.put(targetDate, string);
                            WLLogger.log(Level.INFO, "UpcomingEvents - loaded date \"" + targetDate + "\" (took " + (System.currentTimeMillis()-started) + "ms)");
                            handler.handle(string);
                        }
                    });
                }
                break;
            case "holidays":
                getHolidaysResponse(target.substring(key.length()+1), handler);
                break;
            case "happeningnow":
                StreamingEvents.TWITCH.getUpcomingEvents(handler);
                break;
            case "list":
                handler.handle(getListJSON());
                break;
            default:
                final EventController controller = valueOfIdentifier(key);
                if(controller != null) {
                    handler.handle(controller.getUpcomingEvent(values[1]));
                }
                break;
        }
    }

    private void getHolidaysResponse(String value, CompletionHandler handler) {
        switch (value) {
            case "all":
                Holidays.INSTANCE.getAllHolidays(handler);
                break;
            case "near":
                Holidays.INSTANCE.getNearHolidays(handler);
                break;
            default:
                WLUtilities.getCustomCountry(value, new CompletionHandler() {
                    @Override
                    public void handle(Object object) {
                        if(object != null) {
                            final CustomCountry country = (CustomCountry) object;
                            Holidays.INSTANCE.getHolidaysFor(country, handler);
                        }
                    }
                });
                break;
        }
    }

    private void getEvents(CustomCountry country, CompletionHandler handler) {
        if(countries.containsKey(country)) {
            handler.handle(countries.get(country));
        } else {
            final List<EventController> events = getEventsFromCountry(country);
            final EventController last = events.get(events.size()-1);
            final StringBuilder builder = new StringBuilder("[");
            isFirst = true;
            for(EventController controller : events) {
                controller.getUpcomingEvents(new CompletionHandler() {
                    @Override
                    public void handle(Object object) {
                        final String identifier = controller.getType().name().toLowerCase(), json = (String) object;
                        final String string = "\"" + identifier + "\":" + json;
                        builder.append(isFirst ? "" : ",").append(string);
                        isFirst = false;

                        if(controller.equals(last)) {
                            builder.append("]");
                            final String value = builder.toString();
                            countries.put(country, value);
                            handler.handle(value);
                        }
                    }
                });
            }
        }
    }
    public void getEvents(EventController controller, CompletionHandler handler) {
        if(jsons.containsKey(controller)) {
            handler.handle(jsons.get(controller));
        } else {
            refreshEvents(controller, handler);
        }
    }

    private void getEventsFrom(EventDate date, CompletionHandler handler) {
        final int max = CONTROLLERS.length;
        COMPLETED_HANDLERS.put(date, 0);
        final StringBuilder builder = new StringBuilder("{");
        for(EventController controller : CONTROLLERS) {
            controller.getEventsFromDate(date, new CompletionHandler() {
                @Override
                public void handle(Object object) {
                    final String value = "\"" + controller.getType().name().toLowerCase() + "\":" + object.toString();
                    builder.append(getCompletedHandlers(date) == 0 ? "" : ",").append(value);
                    completedHandler(date);
                    if(getCompletedHandlers(date) == max) {
                        COMPLETED_HANDLERS.remove(date);
                        final String string = builder.append("}").toString();
                        handler.handle(string);
                    }
                }
            });
        }
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
                final CountryBackendID backendID  = event.getCountryBackendID();
                return backendID == null || !backendID.getValue().equalsIgnoreCase(country.getBackendID());
            });
        }
        return set;
    }

    private synchronized void completedHandler(EventDate date) {
        COMPLETED_HANDLERS.put(date, COMPLETED_HANDLERS.get(date)+1);
    }
    private synchronized int getCompletedHandlers(EventDate date) {
        return COMPLETED_HANDLERS.getOrDefault(date, 0);
    }
}
