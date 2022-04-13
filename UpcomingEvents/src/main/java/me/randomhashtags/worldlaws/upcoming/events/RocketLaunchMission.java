package me.randomhashtags.worldlaws.upcoming.events;

import me.randomhashtags.worldlaws.locale.JSONObjectTranslatable;
import me.randomhashtags.worldlaws.upcoming.UpcomingEventValue;

import java.util.HashSet;

public final class RocketLaunchMission extends JSONObjectTranslatable {

    public RocketLaunchMission(String name, String description, String type) {
        put(UpcomingEventValue.ROCKET_LAUNCH_MISSION_NAME.getKey(), name);
        put(UpcomingEventValue.ROCKET_LAUNCH_MISSION_DESCRIPTION.getKey(), description);
        put(UpcomingEventValue.ROCKET_LAUNCH_MISSION_TYPE.getKey(), type);
    }

    @Override
    public HashSet<String> getTranslatedKeys() {
        return collectKeys(
                UpcomingEventValue.ROCKET_LAUNCH_MISSION_NAME.getKey(),
                UpcomingEventValue.ROCKET_LAUNCH_MISSION_DESCRIPTION.getKey(),
                UpcomingEventValue.ROCKET_LAUNCH_MISSION_TYPE.getKey()
        );
    }
}
