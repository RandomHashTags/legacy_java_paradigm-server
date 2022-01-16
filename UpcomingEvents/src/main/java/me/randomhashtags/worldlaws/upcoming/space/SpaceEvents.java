package me.randomhashtags.worldlaws.upcoming.space;

import me.randomhashtags.worldlaws.EventDate;
import me.randomhashtags.worldlaws.EventSource;
import me.randomhashtags.worldlaws.EventSources;
import me.randomhashtags.worldlaws.RequestMethod;
import me.randomhashtags.worldlaws.stream.ParallelStream;
import me.randomhashtags.worldlaws.upcoming.LoadedUpcomingEventController;
import me.randomhashtags.worldlaws.upcoming.UpcomingEventType;
import me.randomhashtags.worldlaws.upcoming.events.SpaceEvent;
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
        final JSONObject json = requestJSONObject(url, RequestMethod.GET);
        if(json != null) {
            final JSONArray resultsArray = json.getJSONArray("results");
            final int max = resultsArray.length();
            if(max > 0) {
                final LocalDate endingDate = LocalDate.now().plusWeeks(1);
                final EventSources sources = new EventSources(new EventSource("The Space Devs", "https://thespacedevs.com"));
                ParallelStream.stream(resultsArray.spliterator(), obj -> {
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

                        final String newsURL = resultJSON.get("news_url") instanceof String ? resultJSON.getString("news_url") : null;
                        final String videoURL = resultJSON.get("video_url") instanceof String ? resultJSON.getString("video_url") : null;

                        final String id = getEventDateIdentifier(dateString, title);
                        final SpaceEvent event = new SpaceEvent(title, description, imageURL, location, newsURL, videoURL, sources);
                        putLoadedPreUpcomingEvent(id, event.toPreUpcomingEventJSON(eventType, id, location));
                        putUpcomingEvent(id, event.toString());
                    }
                });
            }
        }
    }
}
