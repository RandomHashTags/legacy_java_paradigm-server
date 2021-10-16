package me.randomhashtags.worldlaws.upcoming.events;

import me.randomhashtags.worldlaws.EventSources;
import me.randomhashtags.worldlaws.LocalServer;
import me.randomhashtags.worldlaws.upcoming.UpcomingEventType;

public final class RocketLaunchEvent extends UpcomingEvent {
    private final String status, windowStart, windowEnd;
    private final boolean exactDay, exactTime;
    private final int probability;
    private final RocketLaunchMission mission;

    public RocketLaunchEvent(String name, String status, String location, boolean exactDay, boolean exactTime, int probability, String rocketImageURL, RocketLaunchMission mission, String windowStart, String windowEnd, EventSources sources) {
        super(name, null, rocketImageURL, location, null, sources);
        this.status = status;
        this.exactDay = exactDay;
        this.exactTime = exactTime;
        this.probability = probability;
        this.mission = mission;
        this.windowStart = LocalServer.fixEscapeValues(windowStart);
        this.windowEnd = LocalServer.fixEscapeValues(windowEnd);
    }

    @Override
    public UpcomingEventType getType() {
        return UpcomingEventType.SPACE_ROCKET_LAUNCH;
    }

    @Override
    public String getPropertiesJSONObject() {
        return "{" +
                (mission != null ? "\"mission\":" + mission.toString() + "," : "") +
                "\"windowStart\":\"" + windowStart + "\"," +
                "\"windowEnd\":\"" + windowEnd + "\"," +
                "\"exactDay\":" + exactDay + "," +
                "\"exactTime\":" + exactTime + "," +
                "\"status\":\"" + status + "\"," +
                "\"probability\":" + probability +
                "}";
    }
}
