package me.randomhashtags.worldlaws.upcoming.events;

import me.randomhashtags.worldlaws.EventSources;
import me.randomhashtags.worldlaws.Jsoupable;
import me.randomhashtags.worldlaws.LocalServer;
import me.randomhashtags.worldlaws.PreUpcomingEvent;
import me.randomhashtags.worldlaws.upcoming.UpcomingEventType;
import org.json.JSONArray;

public abstract class UpcomingEvent implements Jsoupable {

    private final String title, description, imageURL, location;
    private final JSONArray youtubeVideoIDs;
    private final EventSources sources;

    public UpcomingEvent(String title, String description, String imageURL, String location, JSONArray youtubeVideoIDs, EventSources sources) {
        this.title = title;
        this.description = description;
        this.imageURL = imageURL;
        this.location = location;
        this.youtubeVideoIDs = youtubeVideoIDs;
        this.sources = sources;
    }

    public abstract UpcomingEventType getType();
    public abstract String getPropertiesJSONObject();

    public String getTitle() {
        return title;
    }
    public String getDescription() {
        return description;
    }
    public String getImageURL() {
        return imageURL;
    }
    public String getLocation() {
        return location;
    }
    public JSONArray getYouTubeVideoIDs() {
        return youtubeVideoIDs;
    }
    public EventSources getSources() {
        return sources;
    }

    public String toPreUpcomingEventJSON(UpcomingEventType type, String id, String tag) {
        return new PreUpcomingEvent(id, getTitle(), null, tag).toStringWithImageURL(type, getImageURL());
    }

    @Override
    public String toString() {
        final String imageURL = getImageURL(), properties = getPropertiesJSONObject(), location = getLocation();
        final String description = LocalServer.fixEscapeValues(removeReferences(getDescription()));
        final JSONArray youtubeVideoIDs = getYouTubeVideoIDs();
        return "{" +
                "\"title\":\"" + LocalServer.fixEscapeValues(getTitle()) + "\"," +
                (description != null ? "\"description\":\"" + description + "\"," : "") +
                (youtubeVideoIDs != null ? "\"youtubeVideoIDs\":" + youtubeVideoIDs.toString() + "," : "") +
                (location != null ? "\"location\":\"" + location + "\"," : "") +
                (imageURL != null ? "\"imageURL\":\"" + imageURL + "\"," : "") +
                (properties != null ? "\"properties\":" + properties + "," : "") +
                "\"sources\":" + getSources().toString() +
                "}";
    }
}
