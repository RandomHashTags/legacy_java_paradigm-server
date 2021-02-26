package me.randomhashtags.worldlaws.event;

import me.randomhashtags.worldlaws.EventSources;
import me.randomhashtags.worldlaws.LocalServer;
import me.randomhashtags.worldlaws.UpcomingEventType;

public interface UpcomingEvent {
    UpcomingEventType getType();
    EventDate getDate();
    String getTitle();
    String getDescription();
    String getImageURL();
    String getLocation();
    EventSources getSources();
    String getPropertiesJSONObject();

    default String toJSON() {
        final String imageURL = getImageURL(), properties = getPropertiesJSONObject();
        return "{" +
                "\"type\":\"" + getType().name() + "\"," +
                "\"date\":" + getDate().toString() + "," +
                "\"title\":\"" + LocalServer.fixEscapeValues(getTitle()) + "\"," +
                "\"description\":\"" + LocalServer.fixEscapeValues(getDescription()) + "\"," +
                "\"location\":\"" + getLocation() + "\"," +
                (imageURL != null ? "\"imageURL\":\"" + imageURL + "\"," : "") +
                (properties != null ? "\"properties\":" + properties + "," : "") +
                "\"sources\":" + getSources().toString() + "," +
                "}";
    }
}
