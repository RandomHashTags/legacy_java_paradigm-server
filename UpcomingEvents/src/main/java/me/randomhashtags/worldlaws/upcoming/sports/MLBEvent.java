package me.randomhashtags.worldlaws.upcoming.sports;

import me.randomhashtags.worldlaws.EventSources;
import me.randomhashtags.worldlaws.upcoming.UpcomingEvent;
import me.randomhashtags.worldlaws.upcoming.UpcomingEventType;

public final class MLBEvent extends UpcomingEvent {
    private final String awayTeam, homeTeam;

    public MLBEvent(String event, String awayTeam, String homeTeam, String location, EventSources sources) {
        super(event, null, null, location, null, sources);
        this.awayTeam = awayTeam;
        this.homeTeam = homeTeam;
    }

    @Override
    public UpcomingEventType getType() {
        return UpcomingEventType.SPORT_MLB;
    }

    @Override
    public String getPropertiesJSONObject() {
        return "{" +
                "\"awayTeam\":" + awayTeam + "," +
                "\"homeTeam\":" + homeTeam +
                "}";
    }
}
