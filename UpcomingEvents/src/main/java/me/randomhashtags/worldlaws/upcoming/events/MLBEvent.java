package me.randomhashtags.worldlaws.upcoming.events;

import me.randomhashtags.worldlaws.EventDate;
import me.randomhashtags.worldlaws.EventSources;
import me.randomhashtags.worldlaws.locale.JSONObjectTranslatable;
import me.randomhashtags.worldlaws.upcoming.UpcomingEventType;
import me.randomhashtags.worldlaws.upcoming.UpcomingEventValue;
import org.json.JSONObject;

public final class MLBEvent extends UpcomingEvent {
    private final String awayTeam, homeTeam;

    public MLBEvent(JSONObject json) {
        super(json);
        final JSONObject properties = json.getJSONObject("properties");
        awayTeam = properties.getString(UpcomingEventValue.MLB_TEAM_AWAY.getKey());
        homeTeam = properties.getString(UpcomingEventValue.MLB_TEAM_HOME.getKey());
        insertProperties();
    }
    public MLBEvent(EventDate date, String event, String awayTeam, String homeTeam, String location, EventSources sources) {
        super(date, event, null, null, location, null, sources);
        this.awayTeam = awayTeam;
        this.homeTeam = homeTeam;
        insertProperties();
    }

    @Override
    public UpcomingEventType getType() {
        return UpcomingEventType.SPORT_MLB;
    }

    @Override
    public JSONObjectTranslatable getPropertiesJSONObject() {
        final JSONObjectTranslatable json = new JSONObjectTranslatable();
        json.put(UpcomingEventValue.MLB_TEAM_AWAY.getKey(), awayTeam);
        json.put(UpcomingEventValue.MLB_TEAM_HOME.getKey(), homeTeam);
        return json;
    }
}
