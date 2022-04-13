package me.randomhashtags.worldlaws.upcoming.events;

import me.randomhashtags.worldlaws.EventDate;
import me.randomhashtags.worldlaws.EventSources;
import me.randomhashtags.worldlaws.locale.JSONObjectTranslatable;
import me.randomhashtags.worldlaws.upcoming.UpcomingEventType;
import me.randomhashtags.worldlaws.upcoming.UpcomingEventValue;
import org.json.JSONObject;

public final class RocketLaunchEvent extends UpcomingEvent {
    private final String status, windowStart, windowEnd;
    private final boolean exactDay, exactTime;
    private final int probability;
    private final RocketLaunchMission mission;

    public RocketLaunchEvent(JSONObject json) {
        super(json);
        final JSONObject properties = json.getJSONObject("properties");
        probability = properties.getInt(UpcomingEventValue.ROCKET_LAUNCH_PROBABILITY.getKey());
        windowStart = properties.getString(UpcomingEventValue.ROCKET_LAUNCH_WINDOW_START.getKey());
        windowEnd = properties.getString(UpcomingEventValue.ROCKET_LAUNCH_WINDOW_END.getKey());
        exactDay = properties.getBoolean(UpcomingEventValue.ROCKET_LAUNCH_EXACT_DAY.getKey());
        exactTime = properties.getBoolean(UpcomingEventValue.ROCKET_LAUNCH_EXACT_TIME.getKey());
        status = properties.getString(UpcomingEventValue.ROCKET_LAUNCH_STATUS.getKey());
        final String missionName = properties.optString(UpcomingEventValue.ROCKET_LAUNCH_MISSION_NAME.getKey(), null);
        final String missionDescription = properties.optString(UpcomingEventValue.ROCKET_LAUNCH_MISSION_DESCRIPTION.getKey(), null);
        final String missionType = properties.optString(UpcomingEventValue.ROCKET_LAUNCH_MISSION_TYPE.getKey(), null);
        if(missionName != null) {
            mission = new RocketLaunchMission(missionName, missionDescription, missionType);
        } else {
            mission = null;
        }
        insertProperties();
    }
    public RocketLaunchEvent(EventDate date, String name, String status, String location, boolean exactDay, boolean exactTime, int probability, String rocketImageURL, RocketLaunchMission mission, String windowStart, String windowEnd, EventSources sources) {
        super(date, name, null, rocketImageURL, location, null, sources);
        this.status = status;
        this.exactDay = exactDay;
        this.exactTime = exactTime;
        this.probability = probability;
        this.mission = mission;
        this.windowStart = windowStart;
        this.windowEnd = windowEnd;
        insertProperties();
    }

    @Override
    public UpcomingEventType getType() {
        return UpcomingEventType.SPACE_ROCKET_LAUNCH;
    }

    @Override
    public JSONObjectTranslatable getPropertiesJSONObject() {
        final JSONObjectTranslatable json = new JSONObjectTranslatable();
        if(mission != null) {
            for(String key : mission.keySet()) {
                json.put(key, mission.get(key));
            }
        }
        final String probabilityString = probability >= 0 ? " (" + probability + "% probability of happening)" : "";
        json.put(UpcomingEventValue.ROCKET_LAUNCH_PROBABILITY.getKey(), probabilityString);
        json.put(UpcomingEventValue.ROCKET_LAUNCH_WINDOW_START.getKey(), windowStart);
        json.put(UpcomingEventValue.ROCKET_LAUNCH_WINDOW_END.getKey(), windowEnd);
        json.put(UpcomingEventValue.ROCKET_LAUNCH_EXACT_DAY.getKey(), exactDay);
        json.put(UpcomingEventValue.ROCKET_LAUNCH_EXACT_TIME.getKey(), exactTime);
        json.put(UpcomingEventValue.ROCKET_LAUNCH_STATUS.getKey(), status);
        return json;
    }
}
