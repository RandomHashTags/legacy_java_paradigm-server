package me.randomhashtags.worldlaws;

import java.util.HashMap;

public final class PreUpcomingEvent {
    private final String id, title, url, tag;
    private final HashMap<String, Object> customValues;

    public PreUpcomingEvent(String id, String title, String url, String tag) {
        this(id, title, url, tag, null);
    }
    public PreUpcomingEvent(String id, String title, String url, String tag, HashMap<String, Object> customValues) {
        this.id = id;
        this.title = LocalServer.fixEscapeValues(title);
        this.url = url;
        this.tag = LocalServer.fixEscapeValues(tag);
        this.customValues = customValues;
    }

    public String getID() {
        return id;
    }
    public String getTitle() {
        return title;
    }
    public String getURL() {
        return url;
    }
    public String getTag() {
        return tag;
    }

    public Object getCustomValue(String key) {
        return customValues != null ? customValues.getOrDefault(key, null) : null;
    }

    public String toStringWithImageURL(String imageURL) {
        final String[] values = id.split("\\.");
        return "\"" + id.substring(values[0].length()+1) + "\":{" +
                "\"title\":\"" + title + "\"," +
                (imageURL != null ? "\"imageURL\":\"" + imageURL + "\"," : "") +
                "\"tag\":\"" + tag + "\"" +
                "}";
    }
}
