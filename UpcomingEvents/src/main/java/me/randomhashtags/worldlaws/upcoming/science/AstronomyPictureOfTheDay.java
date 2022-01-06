package me.randomhashtags.worldlaws.upcoming.science;

import me.randomhashtags.worldlaws.*;
import me.randomhashtags.worldlaws.service.NASAService;
import me.randomhashtags.worldlaws.upcoming.LoadedUpcomingEventController;
import me.randomhashtags.worldlaws.upcoming.UpcomingEventType;
import me.randomhashtags.worldlaws.upcoming.events.APODEvent;
import org.json.JSONObject;

import java.time.LocalDate;
import java.time.Month;

public final class AstronomyPictureOfTheDay extends LoadedUpcomingEventController {

    @Override
    public UpcomingEventType getType() {
        return UpcomingEventType.ASTRONOMY_PICTURE_OF_THE_DAY;
    }

    @Override
    public void load(CompletionHandler handler) {
        final String apiKey = NASAService.getNASA_APIKey();
        requestJSONObject("https://api.nasa.gov/planetary/apod?api_key=" + apiKey, RequestMethod.GET, CONTENT_HEADERS, new CompletionHandler() {
            @Override
            public void handleJSONObject(JSONObject json) {
                if(json != null) {
                    final UpcomingEventType type = getType();
                    final EventSources sources = new EventSources(
                            new EventSource("NASA: Astronomy Picture of the Day", "https://apod.nasa.gov")
                    );
                    final String copyright = json.has("copyright") ? json.getString("copyright") : null,
                            description = json.getString("explanation"),
                            title = json.getString("title"),
                            imageURL = json.getString("hdurl"),
                            targetDate = json.getString("date")
                    ;

                    final String[] targetDateValues = targetDate.split("-");
                    final int year = Integer.parseInt(targetDateValues[0]), day = Integer.parseInt(targetDateValues[2]);
                    final Month month = Month.of(Integer.parseInt(targetDateValues[1]));
                    final LocalDate date = LocalDate.of(year, month, day);
                    final String dateString = new EventDate(date).getDateString();

                    final APODEvent event = new APODEvent(title, description, imageURL, copyright, sources);
                    final String identifier = getEventDateIdentifier(dateString, title);
                    putLoadedPreUpcomingEvent(identifier, event.toPreUpcomingEventJSON(type, identifier, copyright != null ? "Copyright: " + copyright : null));
                    putUpcomingEvent(identifier, event.toString());
                }
                handler.handleString(null);
            }
        });
    }
}
