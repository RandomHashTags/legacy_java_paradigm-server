package me.randomhashtags.worldlaws.upcoming.entertainment;

import me.randomhashtags.worldlaws.EventSources;
import me.randomhashtags.worldlaws.upcoming.UpcomingEvent;

public final class VideoGameEvent implements UpcomingEvent {
    private final String title, description, coverArtURL, platforms;
    private final EventSources sources;

    public VideoGameEvent(String title, String description, String coverArtURL, String platforms, EventSources sources) {
        this.title = title;
        this.description = description;
        this.coverArtURL = coverArtURL;
        this.platforms = platforms;
        this.sources = sources;
    }

    @Override
    public String getTitle() {
        return title;
    }
    @Override
    public String getDescription() {
        return description;
    }
    @Override
    public String getImageURL() {
        return coverArtURL;
    }
    @Override
    public String getLocation() {
        return null;
    }
    @Override
    public EventSources getSources() {
        return sources;
    }

    @Override
    public String getPropertiesJSONObject() {
        return "{" +
                "\"platforms\":" + platforms +
                "}";
    }
}