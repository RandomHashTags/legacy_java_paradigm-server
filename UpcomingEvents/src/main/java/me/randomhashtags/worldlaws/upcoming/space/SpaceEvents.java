package me.randomhashtags.worldlaws.upcoming.space;

import me.randomhashtags.worldlaws.*;
import me.randomhashtags.worldlaws.upcoming.LoadedUpcomingEventController;
import me.randomhashtags.worldlaws.upcoming.UpcomingEventType;
import me.randomhashtags.worldlaws.upcoming.events.SpaceEvent;
import org.json.JSONArray;
import org.json.JSONObject;

import java.time.LocalDate;
import java.time.Month;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.StreamSupport;

public final class SpaceEvents extends LoadedUpcomingEventController {

    @Override
    public UpcomingEventType getType() {
        return UpcomingEventType.SPACE_EVENT;
    }

    @Override
    public void load(CompletionHandler handler) {
        final UpcomingEventType eventType = getType();
        final String url = "https://ll.thespacedevs.com/2.0.0/event/upcoming/?format=json&limit=50&offset=0";
        requestJSONObject(url, RequestMethod.GET, new CompletionHandler() {
            @Override
            public void handleJSONObject(JSONObject json) {
                if(json != null) {
                    final JSONArray resultsArray = json.getJSONArray("results");
                    final int max = resultsArray.length();
                    if(max == 0) {
                        handler.handleString(null);
                    } else {
                        final LocalDate endingDate = LocalDate.now().plusWeeks(1);
                        final EventSources sources = new EventSources(new EventSource("The Space Devs", "https://thespacedevs.com"));
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
                                final String location = resultJSON.get("location") instanceof String ? resultJSON.getString("location") : null;
                                final String imageURL = resultJSON.getString("feature_image");

                                final String id = getEventDateIdentifier(dateString, title);
                                final SpaceEvent event = new SpaceEvent(title, description, imageURL, location, sources);
                                putLoadedPreUpcomingEvent(id, event.toPreUpcomingEventJSON(eventType, id, location));
                                putUpcomingEvent(id, event.toString());
                            }

                            if(completed.addAndGet(1) == max) {
                                handler.handleString(null);
                            }
                        });
                    }
                } else {
                    handler.handleString(null);
                }
            }
        });
    }
}
