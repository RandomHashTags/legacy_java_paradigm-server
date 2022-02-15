package me.randomhashtags.worldlaws.upcoming.events;

import me.randomhashtags.worldlaws.EventDate;
import me.randomhashtags.worldlaws.EventSources;
import me.randomhashtags.worldlaws.upcoming.UpcomingEventType;

public final class PresentationEvent extends UpcomingEvent {

    private final String tag;
    private final EventDate date;
    private final EventSources externalLinks;

    public PresentationEvent(EventDate date, String title, String description, String imageURL, String location, String tag, EventSources externalLinks) {
        super(title, description, imageURL, location, null, null);
        this.date = date;
        this.tag = tag;
        this.externalLinks = externalLinks;
    }

    @Override
    public UpcomingEventType getType() {
        return UpcomingEventType.PRESENTATION;
    }

    public EventDate getDate() {
        return date;
    }
    public String getTag() {
        return tag;
    }
    public EventSources getExternalLinks() {
        return externalLinks;
    }

    @Override
    public String getPropertiesJSONObject() {
        return null;
    }
}
