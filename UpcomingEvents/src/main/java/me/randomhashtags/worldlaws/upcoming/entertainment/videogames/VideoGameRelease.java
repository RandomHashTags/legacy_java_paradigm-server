package me.randomhashtags.worldlaws.upcoming.entertainment.videogames;

import me.randomhashtags.worldlaws.EventSources;
import me.randomhashtags.worldlaws.upcoming.UpcomingEventType;
import me.randomhashtags.worldlaws.upcoming.events.UpcomingEvent;

import java.util.HashSet;

public class VideoGameRelease extends UpcomingEvent {
    private final HashSet<String> genres;

    public VideoGameRelease(String title, String description, String imageURL, HashSet<String> genres, EventSources sources) {
        super(title, description, imageURL, null, null, sources);
        this.genres = genres;
    }

    private String getGenresArray() {
        final StringBuilder builder = new StringBuilder("[");
        boolean isFirst = true;
        for(String genre : genres) {
            builder.append(isFirst ? "" : ",").append("\"").append(genre).append("\"");
            isFirst = false;
        }
        builder.append("]");
        return builder.toString();
    }

    @Override
    public UpcomingEventType getType() {
        return null;
    }

    @Override
    public String getPropertiesJSONObject() {
        return "{" +
                "\"genres\":" + getGenresArray() +
                "}";
    }
}
