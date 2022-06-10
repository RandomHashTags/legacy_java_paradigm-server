package me.randomhashtags.worldlaws.upcoming.science;

import me.randomhashtags.worldlaws.EventDate;
import me.randomhashtags.worldlaws.EventSource;
import me.randomhashtags.worldlaws.EventSources;
import me.randomhashtags.worldlaws.service.NASAService;
import me.randomhashtags.worldlaws.upcoming.LoadedUpcomingEventController;
import me.randomhashtags.worldlaws.upcoming.UpcomingEventType;
import me.randomhashtags.worldlaws.upcoming.events.APODEvent;
import me.randomhashtags.worldlaws.upcoming.events.UpcomingEvent;
import org.json.JSONObject;

import java.time.LocalDate;
import java.time.Month;

public final class AstronomyPictureOfTheDay extends LoadedUpcomingEventController {

    @Override
    public UpcomingEventType getType() {
        return UpcomingEventType.ASTRONOMY_PICTURE_OF_THE_DAY;
    }

    @Override
    public void load() {
        final String apiKey = NASAService.getNASA_APIKey();
        final JSONObject json = requestJSONObject("https://api.nasa.gov/planetary/apod?api_key=" + apiKey);
        if(json != null) {
            final UpcomingEventType type = getType();
            final EventSources sources = new EventSources(
                    new EventSource("NASA: Astronomy Picture of the Day", "https://apod.nasa.gov")
            );
            final String copyright = json.optString("copyright", null),
                    explanation = json.optString("explanation", null),
                    title = json.getString("title"),
                    targetDate = json.getString("date")
            ;

            final String imageURL = json.optString("hdurl", null);
            final String videoURL = json.getString("media_type").equals("video") ? json.getString("url") : null;

            final String[] targetDateValues = targetDate.split("-");
            final int year = Integer.parseInt(targetDateValues[0]), day = Integer.parseInt(targetDateValues[2]);
            final Month month = Month.of(Integer.parseInt(targetDateValues[1]));
            final LocalDate date = LocalDate.of(year, month, day);
            final EventDate eventDate = new EventDate(date);

            final APODEvent event = new APODEvent(eventDate, title, explanation, imageURL, copyright, videoURL, sources);
            final String identifier = getEventDateIdentifier(eventDate.getDateString(), title);
            putLoadedPreUpcomingEvent(event.toPreUpcomingEventJSON(type, identifier, copyright != null ? "Copyright: " + copyright : null));
            putUpcomingEvent(identifier, event);
        }
    }

    @Override
    public UpcomingEvent parseUpcomingEvent(JSONObject json) {
        return new APODEvent(json);
    }
}
