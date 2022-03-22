package me.randomhashtags.worldlaws.upcoming.events;

import me.randomhashtags.worldlaws.EventDate;
import me.randomhashtags.worldlaws.EventSources;
import me.randomhashtags.worldlaws.locale.JSONObjectTranslatable;
import me.randomhashtags.worldlaws.upcoming.UpcomingEventType;
import org.json.JSONObject;

public final class PresentationEvent extends UpcomingEvent {

    private final String tag;
    private final EventSources externalLinks;

    public PresentationEvent(JSONObject json) {
        super(json);
        final JSONObject properties = json.getJSONObject("properties");
        tag = properties.getString("tag");
        final JSONObject externalLinksJSON = properties.optJSONObject("externalLinks", null);
        externalLinks = externalLinksJSON != null ? new EventSources(externalLinksJSON) : null;
        insertProperties();
    }
    public PresentationEvent(EventDate date, String title, String description, String imageURL, String location, String tag, EventSources externalLinks) {
        super(date, title, description, imageURL, location, null, null);
        this.tag = tag;
        this.externalLinks = externalLinks;
        insertProperties();
    }

    @Override
    public UpcomingEventType getType() {
        return UpcomingEventType.PRESENTATION;
    }


    public String getTag() {
        return tag;
    }
    public EventSources getExternalLinks() {
        return externalLinks;
    }

    @Override
    public JSONObjectTranslatable getPropertiesJSONObject() {
        final JSONObjectTranslatable json = new JSONObjectTranslatable();
        json.put("tag", tag);
        if(externalLinks != null && !externalLinks.isEmpty()) {
            json.put("externalLinks", externalLinks.toJSONObject());
        }
        return json;
    }
}
