package me.randomhashtags.worldlaws.upcoming.space;

import me.randomhashtags.worldlaws.EventDate;
import me.randomhashtags.worldlaws.EventSource;
import me.randomhashtags.worldlaws.EventSources;
import me.randomhashtags.worldlaws.stream.CompletableFutures;
import me.randomhashtags.worldlaws.upcoming.LoadedUpcomingEventController;
import me.randomhashtags.worldlaws.upcoming.UpcomingEventType;
import me.randomhashtags.worldlaws.upcoming.events.SpaceEvent;
import me.randomhashtags.worldlaws.upcoming.events.UpcomingEvent;
import org.json.JSONArray;
import org.json.JSONObject;

import java.time.LocalDate;
import java.time.Month;

public final class SpaceEvents extends LoadedUpcomingEventController {

    @Override
    public UpcomingEventType getType() {
        return UpcomingEventType.SPACE_EVENT;
    }

    @Override
    public void load() {
        final UpcomingEventType eventType = getType();
        final String url = "https://ll.thespacedevs.com/2.0.0/event/upcoming/?format=json&limit=50&offset=0";
        final JSONObject json = requestJSONObject(url);
        if(json != null) {
            final JSONArray resultsArray = json.getJSONArray("results");
            final int max = resultsArray.length();
            if(max > 0) {
                final LocalDate endingDate = LocalDate.now().plusWeeks(1);
                final EventSources sources = new EventSources(new EventSource("The Space Devs", "https://thespacedevs.com"));
                new CompletableFutures<JSONObject>().stream(resultsArray, resultJSON -> {
                    final String[] dateValues = resultJSON.getString("date").split("T")[0].split("-");
                    final int year = Integer.parseInt(dateValues[0]), day = Integer.parseInt(dateValues[2]);
                    final Month month = Month.of(Integer.parseInt(dateValues[1]));
                    final EventDate eventDate = new EventDate(month, day, year);
                    if(eventDate.getLocalDate().isBefore(endingDate)) {
                        final String dateString = eventDate.getDateString();

                        final String title = resultJSON.getString("name");
                        final String description = resultJSON.getString("description");
                        final String location = resultJSON.optString("location", null);
                        final String imageURL = resultJSON.getString("feature_image");

                        final String newsURL = resultJSON.optString("news_url", null);
                        final String videoURL = resultJSON.optString("video_url", null);

                        final String identifier = getEventDateIdentifier(dateString, title);
                        final SpaceEvent event = new SpaceEvent(eventDate, title, description, imageURL, location, newsURL, videoURL, sources);
                        putLoadedPreUpcomingEvent(event.toPreUpcomingEventJSON(eventType, identifier, location));
                        putUpcomingEvent(identifier, event);
                    }
                });
            }
        }
    }

    @Override
    public UpcomingEvent parseUpcomingEvent(JSONObject json) {
        return new SpaceEvent(json);
    }
}
