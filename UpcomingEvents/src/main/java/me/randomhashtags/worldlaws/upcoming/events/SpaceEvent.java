package me.randomhashtags.worldlaws.upcoming.events;

import me.randomhashtags.worldlaws.EventSources;
import me.randomhashtags.worldlaws.upcoming.UpcomingEventType;
import me.randomhashtags.worldlaws.upcoming.UpcomingEventValue;

public final class SpaceEvent extends UpcomingEvent {

    private final String newsURL, videoURL;

    public SpaceEvent(String title, String description, String imageURL, String location, String newsURL, String videoURL, EventSources sources) {
        super(title, description, imageURL, location, null, sources);
        this.newsURL = newsURL;
        this.videoURL = videoURL;
    }

    @Override
    public UpcomingEventType getType() {
        return UpcomingEventType.SPACE_EVENT;
    }

    @Override
    public String getPropertiesJSONObject() {
        final boolean hasVideoURL = videoURL != null;
        return "{" +
                (newsURL != null ? "\"" + UpcomingEventValue.SPACE_EVENT_NEWS_URL.getKey() + "\":\"" + newsURL + "\"" + (hasVideoURL ? "," : "") : "") +
                (hasVideoURL ? "\"" + UpcomingEventValue.SPACE_EVENT_VIDEO_URL.getKey() + "\":\"" + videoURL + "\"" : "") +
                "}";
    }
}
