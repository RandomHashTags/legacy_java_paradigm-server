package me.randomhashtags.worldlaws.upcoming;

import me.randomhashtags.worldlaws.EventSources;
import me.randomhashtags.worldlaws.Jsoupable;
import me.randomhashtags.worldlaws.LocalServer;
import me.randomhashtags.worldlaws.PreUpcomingEvent;
import org.json.JSONArray;

public interface UpcomingEvent extends Jsoupable {
    String getTitle();
    String getDescription();
    String getImageURL();
    String getLocation();
    default JSONArray getYouTubeVideoIDs() {
        return null;
    }
    EventSources getSources();
    String getPropertiesJSONObject();

    default String toPreUpcomingEventJSON(String id, String tag) {
        return new PreUpcomingEvent(id, getTitle(), null, tag).toStringWithImageURL(getImageURL());
    }

    default String toJSON() {
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
