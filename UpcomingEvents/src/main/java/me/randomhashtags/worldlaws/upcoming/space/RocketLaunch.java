package me.randomhashtags.worldlaws.upcoming.space;

import me.randomhashtags.worldlaws.EventSources;
import me.randomhashtags.worldlaws.LocalServer;
import me.randomhashtags.worldlaws.upcoming.UpcomingEvent;

public final class RocketLaunch implements UpcomingEvent {
    private final String name, status, location, rocketImageURL, windowStart, windowEnd;
    private final boolean exactDay, exactTime;
    private final int probability;
    private final RocketLaunchMission mission;
    private final EventSources sources;

    RocketLaunch(String name, String status, String location, boolean exactDay, boolean exactTime, int probability, String rocketImageURL, RocketLaunchMission mission, String windowStart, String windowEnd, EventSources sources) {
        this.name = LocalServer.fixEscapeValues(name);
        this.status = status;
        this.location = location;
        this.exactDay = exactDay;
        this.exactTime = exactTime;
        this.probability = probability;
        this.rocketImageURL = rocketImageURL;
        this.mission = mission;
        this.windowStart = LocalServer.fixEscapeValues(windowStart);
        this.windowEnd = LocalServer.fixEscapeValues(windowEnd);
        this.sources = sources;
    }

    @Override
    public String getTitle() {
        return name;
    }

    @Override
    public String getDescription() {
        return mission != null ? mission.getDescription() : "Mission information about this launch is currently unknown";
    }

    @Override
    public String getImageURL() {
        return rocketImageURL;
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
                "\"windowStart\":\"" + windowStart + "\"," +
                "\"windowEnd\":\"" + windowEnd + "\"," +
                "\"exactDay\":" + exactDay + "," +
                "\"exactTime\":" + exactTime + "," +
                "\"status\":\"" + status + "\"," +
                "\"probability\":" + probability + "," +
                "\"mission\":" + (mission != null ? mission.toString() : "null") +
                "}";
    }
}
