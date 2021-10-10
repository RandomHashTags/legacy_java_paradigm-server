package me.randomhashtags.worldlaws;

import me.randomhashtags.worldlaws.upcoming.UpcomingEventType;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.HashSet;

public final class PreUpcomingEvent {
    private final String id, title, url, tag;
    private final HashSet<String> countries;
    private final HashMap<String, Object> customValues;

    public static PreUpcomingEvent fromUpcomingEventJSON(UpcomingEventType type, String id, JSONObject json) {
        final String title = json.getString("title");
        return new PreUpcomingEvent(id, title, null, getTag(type, title, json));
    }
    private static String getTag(UpcomingEventType type, String title, JSONObject json) {
        final JSONObject properties = json.has("properties") ? json.getJSONObject("properties") : null;
        switch (type) {
            case MOVIE:
                return properties.getString("productionCompany");
            case MUSIC_ALBUM:
                return properties.getString("artist");
            case SPACE_NEAR_EARTH_OBJECT:
                return "NEO: " + title;
            case SPACE_ROCKET_LAUNCH:
                return json.getString("location");
            case SPORT_UFC:
                return json.getString("location");
            case VIDEO_GAME:
                final String string = properties.getJSONArray("platforms").toString();
                return string.substring(1, string.length()-1);
            default:
                return null;
        }
    }

    public PreUpcomingEvent(String id, String title, String url, String tag) {
        this(id, title, url, tag, null);
    }
    public PreUpcomingEvent(String id, String title, String url, String tag, HashSet<String> countries) {
        this(id, title, url, tag, countries, null);
    }
    public PreUpcomingEvent(String id, String title, String url, String tag, HashSet<String> countries, HashMap<String, Object> customValues) {
        this.id = id;
        this.title = LocalServer.fixEscapeValues(title);
        this.url = url;
        this.tag = LocalServer.fixEscapeValues(tag);
        this.countries = countries;
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

    private String getCountriesArray() {
        final StringBuilder builder = new StringBuilder("[");
        boolean isFirst = true;
        for(String country : countries) {
            builder.append(isFirst ? "" : ",").append("\"").append(country).append("\"");
            isFirst = false;
        }
        builder.append("]");
        return builder.toString();
    }

    public String toStringWithImageURL(UpcomingEventType type, String imageURL) {
        final String[] values = id.split("\\.");
        return "\"" + id.substring(values[0].length()+1) + "\":{" +
                (tag != null && !tag.equals(title) ? "\"tag\":\"" + tag + "\"," : "") +
                (imageURL != null ? "\"imageURL\":\"" + type.optimizeImageURL(imageURL) + "\"," : "") +
                (countries != null ? "\"countries\":" + getCountriesArray() + "," : "") +
                "\"title\":\"" + title + "\"" +
                "}";
    }
}
