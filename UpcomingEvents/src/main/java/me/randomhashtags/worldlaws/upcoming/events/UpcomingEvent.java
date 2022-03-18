package me.randomhashtags.worldlaws.upcoming.events;

import me.randomhashtags.worldlaws.*;
import me.randomhashtags.worldlaws.locale.JSONObjectTranslatable;
import me.randomhashtags.worldlaws.upcoming.UpcomingEventController;
import me.randomhashtags.worldlaws.upcoming.UpcomingEventType;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashSet;
import java.util.List;

public abstract class UpcomingEvent extends JSONObjectTranslatable implements Jsoupable {

    private final EventDate date;
    private String customTypeSingularName;

    public UpcomingEvent(JSONObject json) {
        final String dateString = json.getString("eventDate");
        this.date = EventDate.valueOfDateString(dateString);
        put("eventDate", dateString);
        final String[] keys = { "title", "description", "location", "imageURL", "youtubeVideoIDs", "sources" };
        for(String key : keys) {
            if(json.has(key)) {
                put(key, json.get(key));
            }
        }
        setClientKeysToBeRemoved();
    }
    public UpcomingEvent(EventDate date, String title, String description, String imageURL, String location, JSONArray youtubeVideoIDs, EventSources sources) {
        this(date, null, title, description, imageURL, location, youtubeVideoIDs, sources);
    }
    public UpcomingEvent(EventDate date, String customTypeSingularName, String title, String description, String imageURL, String location, JSONArray youtubeVideoIDs, EventSources sources) {
        this.date = date;
        put("eventDate", date.getDateString());
        this.customTypeSingularName = customTypeSingularName;
        put("title", LocalServer.fixEscapeValues(title));
        if(description != null) {
            put("description", LocalServer.fixEscapeValues(LocalServer.removeWikipediaReferences(description)));
        }
        if(location != null) {
            put("location", LocalServer.fixEscapeValues(location));
        }
        if(imageURL != null) {
            put("imageURL", imageURL);
        }
        if(youtubeVideoIDs != null) {
            put("youtubeVideoIDs", youtubeVideoIDs);
        }
        put("sources", sources.toJSONObject());
        setClientKeysToBeRemoved();
    }
    private void setClientKeysToBeRemoved() {
        setRemovedClientKeys("eventDate");
    }
    protected void insertProperties() {
        final JSONObjectTranslatable properties = getPropertiesJSONObject();
        if(properties != null && !properties.isEmpty()) {
            put("properties", properties);
        }
    }

    public EventDate getDate() {
        return date;
    }
    public String getTitle() {
        return optString("title", null);
    }
    public void setSources(EventSources sources) {
        put("sources", sources.toJSONObject());
    }
    public void setCustomTypeSingularName(String customTypeSingularName) {
        this.customTypeSingularName = customTypeSingularName;
    }

    public abstract UpcomingEventType getType();
    public abstract JSONObjectTranslatable getPropertiesJSONObject();

    public LoadedPreUpcomingEvent toPreUpcomingEventJSON(UpcomingEventType type, String id, String tag) {
        return toPreUpcomingEventJSON(type, id, tag, null, null);
    }
    public LoadedPreUpcomingEvent toPreUpcomingEventJSON(UpcomingEventType type, String id, String tag, List<String> countries, JSONObjectTranslatable customValues) {
        final String title = getTitle(), imageURL = optString("imageURL", null);
        return new PreUpcomingEvent(customTypeSingularName, id, title, null, tag, countries, customValues).toLoadedPreUpcomingEventWithImageURL(type, imageURL);
    }
    public void setTitle(String title) {
        put("title", title);
    }

    @Override
    public HashSet<String> getTranslatedKeys() {
        return collectKeys(
                "title",
                "description",
                "location",
                "properties"
        );
    }

    @Override
    public Folder getFolder() {
        return Folder.UPCOMING_EVENTS_IDS;
    }
    @Override
    public String getFileName() {
        final String title = getTitle();
        final String identifier = UpcomingEventController.getEventDateIdentifier(date.getDateString(), title);
        return UpcomingEventController.getUpcomingEventFileName(getType(), getFolder(), identifier);
    }
}
