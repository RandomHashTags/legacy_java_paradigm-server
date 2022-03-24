package me.randomhashtags.worldlaws.recent;

import me.randomhashtags.worldlaws.EventDate;
import me.randomhashtags.worldlaws.EventSources;
import me.randomhashtags.worldlaws.LocalServer;
import me.randomhashtags.worldlaws.locale.JSONObjectTranslatable;
import me.randomhashtags.worldlaws.notifications.RemoteNotificationCategory;

import java.util.Objects;

public class PreRecentEvent {
    protected final RemoteNotificationCategory category;
    protected final EventDate date;
    protected final String title, description, imageURL;
    protected final EventSources sources;
    protected final JSONObjectTranslatable customValues;

    public PreRecentEvent(RemoteNotificationCategory category, EventDate date, String title, String description, String imageURL, EventSources sources) {
        this(category, date, title, description, imageURL, sources, null);
    }
    public PreRecentEvent(RemoteNotificationCategory category, EventDate date, String title, String description, String imageURL, EventSources sources, JSONObjectTranslatable customValues) {
        this.category = category;
        this.date = date;
        this.title = LocalServer.fixEscapeValues(title);
        this.description = LocalServer.fixEscapeValues(description);
        this.imageURL = imageURL;
        this.sources = sources;
        this.customValues = customValues;
    }

    public boolean areEqual(PreRecentEvent right) {
        return date.equals(right.date) && Objects.equals(title, right.title) && Objects.equals(description, right.description);
    }
    public RemoteNotificationCategory getRemoteNotificationCategory() {
        return category;
    }
    public String getIdentifier() {
        return title;
    }
    public String getTitle() {
        return title;
    }
    public String getDescription() {
        return description;
    }
    public EventDate getDate() {
        return date;
    }

    public JSONObjectTranslatable toJSONObject() {
        final JSONObjectTranslatable json = new JSONObjectTranslatable("description");
        if(description != null) {
            json.put("description", description);
        }
        if(imageURL != null) {
            json.put("imageURL", imageURL);
        }
        if(customValues != null) {
            json.put("customValues", customValues);
        }
        json.put("sources", sources.toJSONObject());
        return json;
    }
}
