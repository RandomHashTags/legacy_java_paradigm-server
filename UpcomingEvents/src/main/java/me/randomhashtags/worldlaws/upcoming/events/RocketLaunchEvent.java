package me.randomhashtags.worldlaws.upcoming.events;

import me.randomhashtags.worldlaws.EventSources;
import me.randomhashtags.worldlaws.LocalServer;
import me.randomhashtags.worldlaws.upcoming.UpcomingEventType;
import me.randomhashtags.worldlaws.upcoming.UpcomingEventValue;

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
        final String probabilityString = probability >= 0 ? " (" + probability + "% probability of happening)" : "";
        return "{" +
                (mission != null ? mission.toString() + "," : "") +
                "\"" + UpcomingEventValue.ROCKET_LAUNCH_PROBABILITY.getKey() + "\":\"" + probabilityString + "\"," +
                "\"" + UpcomingEventValue.ROCKET_LAUNCH_WINDOW_START.getKey() + "\":\"" + windowStart + "\"," +
                "\"" + UpcomingEventValue.ROCKET_LAUNCH_WINDOW_END.getKey() + "\":\"" + windowEnd + "\"," +
                "\"" + UpcomingEventValue.ROCKET_LAUNCH_EXACT_DAY.getKey() + "\":\"" + exactDay + "\"," +
                "\"" + UpcomingEventValue.ROCKET_LAUNCH_EXACT_TIME.getKey() + "\":\"" + exactTime + "\"," +
                "\"" + UpcomingEventValue.ROCKET_LAUNCH_STATUS.getKey() + "\":\"" + status + "\"" +
                "}";
    }
}
