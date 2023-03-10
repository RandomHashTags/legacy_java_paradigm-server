package me.randomhashtags.worldlaws;

import me.randomhashtags.worldlaws.locale.JSONObjectTranslatable;
import me.randomhashtags.worldlaws.upcoming.UpcomingEventType;
import me.randomhashtags.worldlaws.upcoming.events.LoadedPreUpcomingEvent;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

public final class PreUpcomingEvent {
    private final String customTypeSingularName, identifier, title, url, tag;
    private final List<String> countries;
    private final JSONObjectTranslatable customValues;
    private ClientEmoji clientEmoji;

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
                return properties.getString("platforms");
            default:
                return null;
        }
    }

    public PreUpcomingEvent(String identifier, String title, String url, String tag) {
        this(identifier, title, url, tag, null);
    }
    public PreUpcomingEvent(String identifier, String title, String url, String tag, List<String> countries) {
        this(identifier, title, url, tag, countries, null);
    }
    public PreUpcomingEvent(String identifier, String title, String url, String tag, List<String> countries, JSONObjectTranslatable customValues) {
        this(null, identifier, title, url, tag, countries, customValues);
    }
    public PreUpcomingEvent(String customTypeSingularName, String identifier, String title, String url, String tag, List<String> countries, JSONObjectTranslatable customValues) {
        this.customTypeSingularName = customTypeSingularName;
        this.identifier = identifier;
        this.title = title;
        this.url = url;
        this.tag = LocalServer.removeWikipediaReferences(tag);
        this.countries = countries;
        this.customValues = customValues;
    }

    public String getDateString() {
        return identifier.split("\\.")[0];
    }
    public EventDate getEventDate() {
        return EventDate.valueOfDateString(getDateString());
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
    public ClientEmoji getClientEmoji() {
        return clientEmoji;
    }
    public void setClientEmoji(ClientEmoji clientEmoji) {
        this.clientEmoji = clientEmoji;
    }

    public Object getCustomValue(String key) {
        return customValues != null ? customValues.opt(key) : null;
    }

    private JSONArray getCountriesArray() {
        final JSONArray array = new JSONArray();
        for(String country : countries) {
            array.put(country);
        }
        return array;
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

    public LoadedPreUpcomingEvent toLoadedPreUpcomingEventWithImageURL(UpcomingEventType type, String imageURL) {
        final JSONObjectTranslatable json = new JSONObjectTranslatable("title", "tag", "customTypeSingularName", "customValues");
        json.put("title", title);
        if(tag != null && !tag.equals(title)) {
            json.put("tag", getTag());
        }
        if(clientEmoji != null) {
            json.put("clientEmoji", clientEmoji.getIdentifier());
        }
        if(customTypeSingularName != null) {
            json.put("customTypeSingularName", customTypeSingularName);
        }
        if(imageURL != null) {
            json.put("imageURL", optimizeImageURL(type, imageURL));
        }
        if(countries != null && !countries.isEmpty()) {
            json.put("countries", getCountriesArray());
        }
        if(customValues != null && !customValues.isEmpty()) {
            json.put("customValues", customValues);
        }
        return new LoadedPreUpcomingEvent(identifier, json);
    }
}
