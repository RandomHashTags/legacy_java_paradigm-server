package me.randomhashtags.worldlaws.event.sports;

import me.randomhashtags.worldlaws.event.*;

public final class SportEvent implements UpcomingEvent {
    private EventDate date;
    private String event, venue, location;
    private EventSources sources;

    public SportEvent(EventDate date, String event, String venue, String location, EventSources sources) {
        this.event = event;
        this.date = date;
        this.venue = venue;
        this.location = location;
        this.sources = sources;
    }

    @Override
    public UpcomingEventType getType() {
        return UpcomingEventType.SPORT_UFC;
    }
    @Override
    public EventDate getDate() {
        return date;
    }
    @Override
    public String getTitle() {
        return event;
    }
    @Override
    public String getDescription() {
        return venue != null ? venue.equalsIgnoreCase("tbd") ? null : venue : null;
    }
    @Override
    public String getLocation() {
        return location;
    }
    @Override
    public EventSources getSources() {
        return sources;
    }
    @Override
    public String getPropertiesJSONObject() {
        return "{" +
                "\"venue\":\"" + venue + "\"" +
                "}";
    }
}
