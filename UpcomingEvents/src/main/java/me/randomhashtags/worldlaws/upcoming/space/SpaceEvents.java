package me.randomhashtags.worldlaws.upcoming.space;

import me.randomhashtags.worldlaws.EventSource;
import me.randomhashtags.worldlaws.EventSources;
import me.randomhashtags.worldlaws.WLUtilities;
import me.randomhashtags.worldlaws.stream.CompletableFutures;
import me.randomhashtags.worldlaws.upcoming.LoadedUpcomingEventController;
import me.randomhashtags.worldlaws.upcoming.UpcomingEventType;
import me.randomhashtags.worldlaws.upcoming.events.SpaceEvent;
import me.randomhashtags.worldlaws.upcoming.events.UpcomingEvent;
import org.json.JSONArray;
import org.json.JSONObject;

import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;

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
                final long endingDate = Instant.now().plusMillis(TimeUnit.DAYS.toMillis(7)).toEpochMilli();
                final EventSources sources = new EventSources(new EventSource("The Space Devs", "https://thespacedevs.com"));
                new CompletableFutures<JSONObject>().stream(resultsArray, resultJSON -> {
                    final String dateString = resultJSON.getString("date");
                    final long exactTimeMilliseconds = WLUtilities.parseDateFormatToMilliseconds(DateTimeFormatter.ISO_INSTANT, dateString);
                    if(exactTimeMilliseconds < endingDate) {
                        final String title = resultJSON.getString("name");
                        final String description = resultJSON.getString("description");
                        final String location = resultJSON.optString("location", null);
                        final String imageURL = resultJSON.getString("feature_image");

                        final String newsURL = resultJSON.optString("news_url", null);
                        final String videoURL = resultJSON.optString("video_url", null);

                        final String identifier = getEventDateIdentifier(exactTimeMilliseconds, title);
                        final SpaceEvent event = new SpaceEvent(exactTimeMilliseconds, title, description, imageURL, location, newsURL, videoURL, sources);
                        putLoadedPreUpcomingEvent(event.toPreUpcomingEventJSON(eventType, identifier, location));
                        putUpcomingEvent(identifier, event);
                    }
                });
            }
        }
    }

    @Override
    public boolean isExactTime() {
        return true;
    }

    @Override
    public UpcomingEvent parseUpcomingEvent(JSONObject json) {
        return new SpaceEvent(json);
    }
}
