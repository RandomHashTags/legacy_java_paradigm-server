package me.randomhashtags.worldlaws.upcoming.events;

import me.randomhashtags.worldlaws.EventSources;
import me.randomhashtags.worldlaws.upcoming.UpcomingEventType;
import me.randomhashtags.worldlaws.upcoming.UpcomingEventValue;

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
                "\"" + UpcomingEventValue.MLB_TEAM_AWAY.getKey() + "\":" + awayTeam + "," +
                "\"" + UpcomingEventValue.MLB_TEAM_HOME.getKey() + "\":" + homeTeam +
                "}";
    }
}
