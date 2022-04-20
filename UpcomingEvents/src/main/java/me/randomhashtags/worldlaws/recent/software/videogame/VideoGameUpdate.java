package me.randomhashtags.worldlaws.recent.software.videogame;

import me.randomhashtags.worldlaws.EventDate;
import me.randomhashtags.worldlaws.EventSources;
import me.randomhashtags.worldlaws.locale.JSONObjectTranslatable;
import me.randomhashtags.worldlaws.notifications.RemoteNotificationCategory;
import me.randomhashtags.worldlaws.recent.PreRecentEvent;

public final class VideoGameUpdate extends PreRecentEvent {
    private final String identifier;
    public VideoGameUpdate(String identifier, EventDate date, String title, String description, String imageURL, EventSources sources) {
        super(RemoteNotificationCategory.VIDEO_GAME_UPDATE, date, title, description, imageURL, sources);
        this.identifier = identifier;
    }

    @Override
    public String getIdentifier() {
        return identifier;
    }


    @Override
    public JSONObjectTranslatable toJSONObject() {
        final JSONObjectTranslatable json = super.toJSONObject();
        json.put("title", title);
        return json;
    }
}
