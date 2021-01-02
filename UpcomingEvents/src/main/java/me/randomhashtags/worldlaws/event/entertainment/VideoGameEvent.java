package me.randomhashtags.worldlaws.event.entertainment;

import me.randomhashtags.worldlaws.event.EventDate;
import me.randomhashtags.worldlaws.EventSources;
import me.randomhashtags.worldlaws.event.UpcomingEvent;
import me.randomhashtags.worldlaws.event.UpcomingEventType;

import java.util.List;

public final class VideoGameEvent implements UpcomingEvent {
    private final EventDate releaseDate;
    private final String title, description, coverArtURL;
    private final List<String> platforms;
    private final EventSources sources;

    public VideoGameEvent(EventDate releaseDate, String title, String description, String coverArtURL, List<String> platforms, EventSources sources) {
        this.releaseDate = releaseDate;
        this.title = title;
        this.description = description;
        this.coverArtURL = coverArtURL;
        this.platforms = platforms;
        this.sources = sources;
    }

    @Override
    public UpcomingEventType getType() {
        return UpcomingEventType.VIDEO_GAME;
    }
    @Override
    public EventDate getDate() {
        return releaseDate;
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

    private String getPlatformsJSON(boolean quotationMarks) {
        final StringBuilder builder = new StringBuilder(quotationMarks ? "[" : "");
        boolean isFirst = true;
        for(String platform : platforms) {
            builder.append(isFirst ? "" : ",").append(quotationMarks ? "\"" : " ").append(platform).append(quotationMarks ? "\"" : "");
            isFirst = false;
        }
        builder.append(quotationMarks ? "]" : "");
        return builder.toString();
    }

    @Override
    public String getPropertiesJSONObject() {
        return "{" +
                "\"coverArtURL\":\"" + coverArtURL + "\"," +
                "\"platforms\":" + getPlatformsJSON(true) +
                "}";
    }

    public String getPlatforms() {
        return getPlatformsJSON(false);
    }
}
