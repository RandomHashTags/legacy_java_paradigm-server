package me.randomhashtags.worldlaws.event;

import me.randomhashtags.worldlaws.EventSources;
import me.randomhashtags.worldlaws.LocalServer;

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
        final String properties = getPropertiesJSONObject();
        return "{" +
                "\"type\":\"" + getType().name() + "\"," +
                "\"date\":" + getDate().toString() + "," +
                "\"title\":\"" + LocalServer.fixEscapeValues(getTitle()) + "\"," +
                "\"description\":\"" + LocalServer.fixEscapeValues(getDescription()) + "\"," +
                "\"location\":\"" + getLocation() + "\"," +
                "\"sources\":" + getSources().toString() + "," +
                "\"properties\":" + (properties != null ? getPropertiesJSONObject() : "\"null\"") +
                "}";
    }
}
