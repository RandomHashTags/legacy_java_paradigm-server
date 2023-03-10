package me.randomhashtags.worldlaws.recent;

import me.randomhashtags.worldlaws.EventDate;
import me.randomhashtags.worldlaws.EventSources;
import me.randomhashtags.worldlaws.LocalServer;
import me.randomhashtags.worldlaws.locale.JSONObjectTranslatable;
import me.randomhashtags.worldlaws.notifications.RemoteNotificationSubcategory;

import java.util.Objects;

public class PreRecentEvent {
    protected final RemoteNotificationSubcategory subcategory;
    protected final EventDate date;
    protected final String title, description, imageURL;
    protected final EventSources sources;
    protected final JSONObjectTranslatable customValues;

    public PreRecentEvent(RemoteNotificationSubcategory subcategory, EventDate date, String title, String description, String imageURL, EventSources sources) {
        this(subcategory, date, title, description, imageURL, sources, null);
    }
    public PreRecentEvent(RemoteNotificationSubcategory subcategory, EventDate date, String title, String description, String imageURL, EventSources sources, JSONObjectTranslatable customValues) {
        this.subcategory = subcategory;
        this.date = date;
        this.title = LocalServer.fixEscapeValues(title);
        this.description = description;
        this.imageURL = imageURL;
        this.sources = sources;
        this.customValues = customValues;
    }

    public boolean areEqual(PreRecentEvent right) {
        return date.equals(right.date) && Objects.equals(title, right.title) && Objects.equals(description, right.description);
    }
    public RemoteNotificationSubcategory getRemoteNotificationSubcategory() {
        return subcategory;
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
