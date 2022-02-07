package me.randomhashtags.worldlaws.recent;

import me.randomhashtags.worldlaws.EventDate;
import me.randomhashtags.worldlaws.EventSources;
import me.randomhashtags.worldlaws.LocalServer;
import me.randomhashtags.worldlaws.notifications.RemoteNotificationCategory;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class PreRecentEvent {
    protected final RemoteNotificationCategory category;
    protected final EventDate date;
    protected final String title, description, imageURL;
    protected final EventSources sources;
    protected final HashMap<String, Object> customValues;

    public PreRecentEvent(RemoteNotificationCategory category, EventDate date, String title, String description, String imageURL, EventSources sources) {
        this(category, date, title, description, imageURL, sources, null);
    }
    public PreRecentEvent(RemoteNotificationCategory category, EventDate date, String title, String description, String imageURL, EventSources sources, HashMap<String, Object> customValues) {
        this.category = category;
        this.date = date;
        this.title = LocalServer.fixEscapeValues(title);
        this.description = LocalServer.fixEscapeValues(description);
        this.imageURL = imageURL;
        this.sources = sources;
        this.customValues = customValues;
    }

    public boolean areEqual(PreRecentEvent right) {
        return date.areEqual(right.date) && Objects.equals(title, right.title) && Objects.equals(description, right.description);
    }
    public RemoteNotificationCategory getRemoteNotificationCategory() {
        return category;
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

    public String getCustomValuesJSON() {
        final StringBuilder builder = new StringBuilder("{");
        boolean isFirst = true;
        for(Map.Entry<String, Object> map : customValues.entrySet()) {
            final Object value = map.getValue();
            builder.append(isFirst ? "" : ",").append("\"").append(map.getKey()).append("\":").append(value instanceof String ? "\"" + value.toString() + "\"" : value.toString());
            isFirst = false;
        }
        builder.append("}");
        return builder.toString();
    }

    @Override
    public String toString() {
        return "\"" + title + "\":{" +
                (description != null ? "\"description\":\"" + description + "\"," : "") +
                (imageURL != null ? "\"imageURL\":\"" + imageURL + "\"," : "") +
                (customValues != null ? "\"customValues\":" + getCustomValuesJSON() + "," : "") +
                "\"sources\":" + sources.toString() +
                "}";
    }
}
