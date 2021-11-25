package me.randomhashtags.worldlaws.upcoming.events;

import me.randomhashtags.worldlaws.EventSources;
import me.randomhashtags.worldlaws.Jsoupable;
import me.randomhashtags.worldlaws.LocalServer;
import me.randomhashtags.worldlaws.PreUpcomingEvent;
import me.randomhashtags.worldlaws.upcoming.UpcomingEventType;
import org.json.JSONArray;

import java.util.HashMap;
import java.util.List;

public abstract class UpcomingEvent implements Jsoupable {

    private final String title, description, imageURL, location;
    private final JSONArray youtubeVideoIDs;
    private final EventSources sources;

    public UpcomingEvent(String title, String description, String imageURL, String location, JSONArray youtubeVideoIDs, EventSources sources) {
        this.title = LocalServer.fixEscapeValues(title);
        this.description = LocalServer.fixEscapeValues(removeReferences(description));
        this.imageURL = imageURL;
        this.location = location;
        this.youtubeVideoIDs = youtubeVideoIDs;
        this.sources = sources;
    }

    public abstract UpcomingEventType getType();
    public abstract String getPropertiesJSONObject();

    public String toPreUpcomingEventJSON(UpcomingEventType type, String id, String tag) {
        return toPreUpcomingEventJSON(type, id, tag, null, null);
    }
    public String toPreUpcomingEventJSON(UpcomingEventType type, String id, String tag, List<String> countries, HashMap<String, Object> customValues) {
        return new PreUpcomingEvent(id, title, null, tag, countries, customValues).toStringWithImageURL(type, imageURL);
    }

    @Override
    public String toString() {
        final String properties = getPropertiesJSONObject();
        return "{" +
                "\"title\":\"" + title + "\"," +
                (description != null ? "\"description\":\"" + description + "\"," : "") +
                (youtubeVideoIDs != null ? "\"youtubeVideoIDs\":" + youtubeVideoIDs.toString() + "," : "") +
                (location != null ? "\"location\":\"" + location + "\"," : "") +
                (imageURL != null ? "\"imageURL\":\"" + imageURL + "\"," : "") +
                (properties != null ? "\"properties\":" + properties + "," : "") +
                "\"sources\":" + sources.toString() +
                "}";
    }
}
