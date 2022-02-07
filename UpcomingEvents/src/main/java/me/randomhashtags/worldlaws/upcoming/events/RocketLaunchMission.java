package me.randomhashtags.worldlaws.upcoming.events;

import me.randomhashtags.worldlaws.LocalServer;
import me.randomhashtags.worldlaws.upcoming.UpcomingEventValue;

public final class RocketLaunchMission {
    private final String name, description, type;

    public RocketLaunchMission(String name, String description, String type) {
        this.name = LocalServer.fixEscapeValues(name);
        this.description = LocalServer.fixEscapeValues(description);
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return
                "\"" + UpcomingEventValue.ROCKET_LAUNCH_MISSION_NAME.getKey() + "\":\"" + name + "\"," +
                "\"" + UpcomingEventValue.ROCKET_LAUNCH_MISSION_DESCRIPTION.getKey() + "\":\"" + description + "\"," +
                "\"" + UpcomingEventValue.ROCKET_LAUNCH_MISSION_TYPE.getKey() + "\":\"" + type + "\"";
    }
}
