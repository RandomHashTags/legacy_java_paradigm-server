package me.randomhashtags.worldlaws.upcoming.events;

import me.randomhashtags.worldlaws.EventSources;
import me.randomhashtags.worldlaws.LocalServer;
import me.randomhashtags.worldlaws.upcoming.UpcomingEventType;
import me.randomhashtags.worldlaws.upcoming.UpcomingEventValue;

public final class APODEvent extends UpcomingEvent {

    private final String copyright, videoURL;

    public APODEvent(String title, String description, String imageURL, String copyright, String videoURL, EventSources sources) {
        super(title, description, imageURL, null, null, sources);
        this.copyright = LocalServer.fixEscapeValues(copyright);
        this.videoURL = videoURL;
    }

    @Override
    public UpcomingEventType getType() {
        return UpcomingEventType.ASTRONOMY_PICTURE_OF_THE_DAY;
    }

    @Override
    public String getPropertiesJSONObject() {
        if(copyright == null && videoURL == null) {
            return null;
        }
        return "{" +
                (copyright != null ? "\"" + UpcomingEventValue.ASTRONOMY_PICTURE_OF_THE_DAY_COPYRIGHT.getKey() + "\":\"" + copyright + "\"" + (videoURL != null ? "," : "") : "") +
                (videoURL != null ? "\"" + UpcomingEventValue.ASTRONOMY_PICTURE_OF_THE_DAY_VIDEO_URL.getKey() + "\":\"" + videoURL + "\"" : "") +
                "}";
    }
}
