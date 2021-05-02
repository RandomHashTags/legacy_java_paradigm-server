package me.randomhashtags.worldlaws.event;

import me.randomhashtags.worldlaws.EventDate;
import me.randomhashtags.worldlaws.EventSources;
import me.randomhashtags.worldlaws.Jsoupable;
import me.randomhashtags.worldlaws.LocalServer;

public interface UpcomingEvent extends Jsoupable {
    EventDate getDate();
    String getTitle();
    String getDescription();
    String getImageURL();
    String getLocation();
    EventSources getSources();
    String getPropertiesJSONObject();

    default String toJSON() {
        final String imageURL = getImageURL(), properties = getPropertiesJSONObject(), location = getLocation();
        final String description = LocalServer.fixEscapeValues(removeReferences(getDescription()));
        return "{" +
                "\"title\":\"" + LocalServer.fixEscapeValues(getTitle()) + "\"," +
                "\"description\":\"" + description + "\"," +
                (location != null ? "\"location\":\"" + location + "\"," : "") +
                (imageURL != null ? "\"imageURL\":\"" + imageURL + "\"," : "") +
                (properties != null ? "\"properties\":" + properties + "," : "") +
                "\"sources\":" + getSources().toString() +
                "}";
    }
}
