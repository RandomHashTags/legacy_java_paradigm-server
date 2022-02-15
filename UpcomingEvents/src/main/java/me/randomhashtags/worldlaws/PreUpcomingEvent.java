package me.randomhashtags.worldlaws;

import me.randomhashtags.worldlaws.upcoming.UpcomingEventType;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class PreUpcomingEvent {
    private final String customTypeSingularName, id, title, url, tag;
    private final List<String> countries;
    private final HashMap<String, Object> customValues;

    public static PreUpcomingEvent fromUpcomingEventJSON(UpcomingEventType type, String id, JSONObject json) {
        final String title = json.getString("title");
        return new PreUpcomingEvent(id, title, null, getTag(type, title, json));
    }
    private static String getTag(UpcomingEventType type, String title, JSONObject json) {
        final JSONObject properties = json.has("properties") ? json.getJSONObject("properties") : null;
        switch (type) {
            case MOVIE:
                return properties.get("productionCompanies").toString();
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
    public PreUpcomingEvent(String id, String title, String url, String tag, List<String> countries) {
        this(id, title, url, tag, countries, null);
    }
    public PreUpcomingEvent(String id, String title, String url, String tag, List<String> countries, HashMap<String, Object> customValues) {
        this(null, id, title, url, tag, countries, customValues);
    }
    public PreUpcomingEvent(String customTypeSingularName, String id, String title, String url, String tag, List<String> countries, HashMap<String, Object> customValues) {
        this.customTypeSingularName = customTypeSingularName;
        this.id = id;
        this.title = LocalServer.fixEscapeValues(title);
        this.url = url;
        this.tag = tag;
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
    public String getUnfixedTag() {
        return tag;
    }
    public String getTag() {
        return LocalServer.fixEscapeValues(tag);
    }
    public List<String> getCountries() {
        return countries;
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

    private String optimizeImageURL(UpcomingEventType type, String imageURL) {
        if(imageURL != null) {
            final String prefix = type.getImageURLPrefix();
            if(prefix != null && imageURL.startsWith(prefix)) {
                return imageURL.substring(prefix.length());
            }
        }
        return imageURL;
    }

    private String getCustomValuesJSON() {
        final StringBuilder builder = new StringBuilder("{");
        boolean isFirst = true;
        for(Map.Entry<String, Object> map : customValues.entrySet()) {
            builder.append(isFirst ? "" : ",").append("\"").append(map.getKey()).append("\":").append(map.getValue().toString());
            isFirst = false;
        }
        builder.append("}");
        return builder.toString();
    }

    public String toStringWithImageURL(UpcomingEventType type, String imageURL) {
        final String[] values = id.split("\\.");
        return "\"" + id.substring(values[0].length() + 1) + "\":{" +
                (customTypeSingularName != null ? "\"customTypeSingularName\":\"" + customTypeSingularName + "\"," : "") +
                (tag != null && !tag.equals(title) ? "\"tag\":\"" + getTag() + "\"," : "") +
                (imageURL != null ? "\"imageURL\":\"" + optimizeImageURL(type, imageURL) + "\"," : "") +
                (countries != null ? "\"countries\":" + getCountriesArray() + "," : "") +
                (customValues != null ? "\"customValues\":" + getCustomValuesJSON() + "," : "") +
                "\"title\":\"" + title + "\"" +
                "}";
    }
}
