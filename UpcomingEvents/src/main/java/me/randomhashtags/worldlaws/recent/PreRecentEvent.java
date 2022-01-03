package me.randomhashtags.worldlaws.recent;

import me.randomhashtags.worldlaws.EventDate;
import me.randomhashtags.worldlaws.EventSources;
import me.randomhashtags.worldlaws.LocalServer;

import java.util.HashMap;
import java.util.Map;

public class PreRecentEvent {
    protected final EventDate date;
    protected final String title, description, imageURL;
    protected final EventSources sources;
    protected final HashMap<String, Object> customValues;

    public PreRecentEvent(EventDate date, String title, String description, String imageURL, EventSources sources) {
        this(date, title, description, imageURL, sources, null);
    }
    public PreRecentEvent(EventDate date, String title, String description, String imageURL, EventSources sources, HashMap<String, Object> customValues) {
        this.date = date;
        this.title = LocalServer.fixEscapeValues(title);
        this.description = LocalServer.fixEscapeValues(description);
        this.imageURL = imageURL;
        this.sources = sources;
        this.customValues = customValues;
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
