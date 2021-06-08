package me.randomhashtags.worldlaws.upcoming.sports;

import me.randomhashtags.worldlaws.EventSources;
import me.randomhashtags.worldlaws.upcoming.UpcomingEvent;

public final class MLBEvent implements UpcomingEvent {
    private final String event, awayTeam, homeTeam, location;
    private final EventSources sources;

    public MLBEvent(String event, String awayTeam, String homeTeam, String location, EventSources sources) {
        this.event = event;
        this.awayTeam = awayTeam;
        this.homeTeam = homeTeam;
        this.location = location;
        this.sources = sources;
    }

    @Override
    public String getTitle() {
        return event;
    }
    @Override
    public String getDescription() {
        return null;
    }
    @Override
    public String getImageURL() {
        return null;
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
                "\"awayTeam\":" + awayTeam + "," +
                "\"homeTeam\":" + homeTeam +
                "}";
    }
}
