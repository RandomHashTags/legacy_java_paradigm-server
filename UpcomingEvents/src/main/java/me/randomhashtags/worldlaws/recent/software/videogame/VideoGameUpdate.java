package me.randomhashtags.worldlaws.recent.software.videogame;

import me.randomhashtags.worldlaws.EventDate;
import me.randomhashtags.worldlaws.EventSources;
import me.randomhashtags.worldlaws.notifications.RemoteNotificationCategory;
import me.randomhashtags.worldlaws.recent.PreRecentEvent;

public final class VideoGameUpdate extends PreRecentEvent {
    public VideoGameUpdate(EventDate date, String title, String description, String imageURL, EventSources sources) {
        super(RemoteNotificationCategory.VIDEO_GAME_UPDATE, date, title, description, imageURL, sources);
    }

    @Override
    public String toString() {
        return "{" +
                "\"title\":\"" + title + "\"," +
                (description != null ? "\"description\":\"" + description + "\"," : "") +
                (imageURL != null ? "\"imageURL\":\"" + imageURL + "\"," : "") +
                "\"sources\":" + sources.toString() +
                "}";
    }
}
