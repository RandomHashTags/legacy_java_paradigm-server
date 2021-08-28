package me.randomhashtags.worldlaws.upcoming.space;

import me.randomhashtags.worldlaws.*;
import me.randomhashtags.worldlaws.upcoming.UpcomingEventController;
import me.randomhashtags.worldlaws.upcoming.UpcomingEventType;
import org.json.JSONArray;
import org.json.JSONObject;

import java.time.LocalDate;
import java.time.Month;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.StreamSupport;

public enum SpaceEvents implements UpcomingEventController {
    INSTANCE;

    private HashMap<String, String> upcomingEvents;

    @Override
    public UpcomingEventType getType() {
        return UpcomingEventType.SPACE_EVENT;
    }

    @Override
    public HashMap<String, PreUpcomingEvent> getPreUpcomingEvents() {
        return null;
    }

    @Override
    public HashMap<String, String> getUpcomingEvents() {
        return upcomingEvents;
    }

    @Override
    public void load(CompletionHandler handler) {
        upcomingEvents = new HashMap<>();

        final String url = "https://ll.thespacedevs.com/2.0.0/event/upcoming/?format=json&limit=50&offset=0";
        requestJSONObject(url, RequestMethod.GET, new CompletionHandler() {
            @Override
            public void handleJSONObject(JSONObject json) {
                if(json != null) {
                    final LocalDate endingDate = LocalDate.now().plusWeeks(1);
                    final EventSources sources = new EventSources(new EventSource("The Space Devs", "https://thespacedevs.com"));
                    final JSONArray resultsArray = json.getJSONArray("results");
                    final int max = resultsArray.length();
                    final AtomicInteger completed = new AtomicInteger(0);
                    StreamSupport.stream(resultsArray.spliterator(), true).forEach(obj -> {
                        final JSONObject resultJSON = (JSONObject) obj;

                        final String[] dateValues = resultJSON.getString("date").split("T")[0].split("-");
                        final int year = Integer.parseInt(dateValues[0]), day = Integer.parseInt(dateValues[2]);
                        final Month month = Month.of(Integer.parseInt(dateValues[1]));
                        final EventDate eventDate = new EventDate(month, day, year);

                        if(eventDate.getLocalDate().isBefore(endingDate)) {
                            final String dateString = eventDate.getDateString();

                            final String title = resultJSON.getString("name");
                            final String description = resultJSON.getString("description");
                            final String location = resultJSON.getString("location");
                            final String imageURL = resultJSON.getString("feature_image");

                            final String id = getEventDateIdentifier(dateString, title);
                            final SpaceEvent event = new SpaceEvent(title, description, imageURL, location, sources);
                            LOADED_PRE_UPCOMING_EVENTS.put(id, event.toPreUpcomingEventJSON(id, location));
                            upcomingEvents.put(id, event.toJSON());
                        }

                        if(completed.addAndGet(1) == max) {
                            handler.handleString(null);
                        }
                    });
                } else {
                    handler.handleString(null);
                }
            }
        });
    }

    @Override
    public void loadUpcomingEvent(String id, CompletionHandler handler) {
    }
}
