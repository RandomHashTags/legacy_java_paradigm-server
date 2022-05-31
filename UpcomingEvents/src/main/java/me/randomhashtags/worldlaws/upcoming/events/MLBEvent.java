package me.randomhashtags.worldlaws.upcoming.events;

import me.randomhashtags.worldlaws.EventDate;
import me.randomhashtags.worldlaws.EventSources;
import me.randomhashtags.worldlaws.locale.JSONObjectTranslatable;
import me.randomhashtags.worldlaws.upcoming.UpcomingEventType;
import me.randomhashtags.worldlaws.upcoming.UpcomingEventValue;
import org.json.JSONObject;

public final class MLBEvent extends UpcomingEvent {
    private final MLBTeamObj awayTeam, homeTeam;

    public MLBEvent(JSONObject json) {
        super(json);
        final JSONObject properties = json.getJSONObject("properties");
        awayTeam = MLBTeamObj.parse(properties.getJSONObject(UpcomingEventValue.MLB_TEAM_AWAY.getKey()));
        homeTeam = MLBTeamObj.parse(properties.getJSONObject(UpcomingEventValue.MLB_TEAM_HOME.getKey()));
    }
    public MLBEvent(EventDate date, String event, MLBTeamObj awayTeam, MLBTeamObj homeTeam, String location, EventSources sources) {
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
        final JSONObjectTranslatable json = new JSONObjectTranslatable(
                UpcomingEventValue.MLB_TEAM_AWAY.getKey(),
                UpcomingEventValue.MLB_TEAM_HOME.getKey()
        );
        json.put(UpcomingEventValue.MLB_TEAM_AWAY.getKey(), awayTeam.toJSONObject());
        json.put(UpcomingEventValue.MLB_TEAM_HOME.getKey(), homeTeam.toJSONObject());
        return json;
    }
}
