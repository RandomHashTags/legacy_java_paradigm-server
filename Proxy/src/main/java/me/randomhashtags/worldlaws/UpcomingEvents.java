package me.randomhashtags.worldlaws;

import me.randomhashtags.worldlaws.event.EventController;
import me.randomhashtags.worldlaws.event.entertainment.Movies;
import me.randomhashtags.worldlaws.event.entertainment.VideoGames;
import me.randomhashtags.worldlaws.event.space.SpaceX;
import me.randomhashtags.worldlaws.event.sports.UFC;
import me.randomhashtags.worldlaws.location.Country;

import java.util.*;

public enum UpcomingEvents implements RestAPI, Jsoupable {
    INSTANCE;

    private volatile String json;
    private volatile StringBuilder builder;
    private volatile boolean isFirst;
    private volatile byte completedHandlers;
    private HashMap<Country, String> countries;
    private volatile HashMap<EventController, String> jsons;

    UpcomingEvents() {
        countries = new HashMap<>();
        jsons = new HashMap<>();
    }

    public void getUpcomingWorldwideEvents(CompletionHandler handler) {
        if(json == null) {
            refreshUpcomingWorldwideEvents(handler);
        } else {
            handler.handle(json);
        }
    }
    public void getUpcomingEvents(Country country, CompletionHandler handler) {
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
                        final String json = (String) object;
                        final String string = "\"" + controller.getIdentifier() + "\":" + json;
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
    public void getUpcomingEvents(EventController controller, CompletionHandler handler) {
        if(jsons.containsKey(controller)) {
            handler.handle(jsons.get(controller));
        } else {
            refreshUpcomingEvents(controller, handler);
        }
    }

    private void refreshUpcomingWorldwideEvents(CompletionHandler handler) {
        final EventController[] controllers = getAllControllers();
        builder = new StringBuilder("{");
        final List<EventController> missing = new ArrayList<>(Arrays.asList(controllers)), existing = new ArrayList<>(Arrays.asList(controllers));
        missing.removeIf(controller -> jsons.containsKey(controller));
        existing.removeIf(controller -> !jsons.containsKey(controller));

        isFirst = true;
        for(EventController controller : existing) {
            final String value = jsons.get(controller);
            final String string = "\"" + controller.getIdentifier() + "\":" + value;
            builder.append(isFirst ? "" : ",").append(string);
            isFirst = false;
        }

        if(!missing.isEmpty()) {
            final int max = missing.size();
            for(EventController controller : missing) {
                new Thread(() -> refreshUpcomingEvents(controller, new CompletionHandler() {
                    @Override
                    public void handle(Object object) {
                        final String value = (String) object;
                        final String string = "\"" + controller.getIdentifier() + "\":" + value;
                        builder.append(isFirst ? "" : ",").append(string);
                        isFirst = false;
                        completedHandlers += 1;

                        if(completedHandlers == max) {
                            builder.append("}");
                            json = builder.toString();
                            handler.handle(json);
                        }
                    }
                })).start();
            }
        } else {
            builder.append("}");
            json = builder.toString();
            handler.handle(json);
        }
    }
    private void refreshUpcomingEvents(EventController controller, CompletionHandler handler) {
        controller.getUpcomingEvents(new CompletionHandler() {
            @Override
            public void handle(Object object) {
                final String json = (String) object;
                jsons.put(controller, json);
                handler.handle(json);
            }
        });
    }

    public EventController[] getAllControllers() {
        return new EventController[] {
                Movies.INSTANCE,
                //NASA.INSTANCE, // javascript problem
                SpaceX.INSTANCE,
                UFC.INSTANCE,
                VideoGames.INSTANCE,
        };
    }
    public List<EventController> getEventsFromCountry(@Nullable Country country) {
        final List<EventController> set = Arrays.asList(getAllControllers());
        if(country != null) {
            set.removeIf(event -> event.getCountryOrigin() != country);
        }
        return set;
    }
}
