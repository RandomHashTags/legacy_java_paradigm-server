package me.randomhashtags.worldlaws.upcoming.events;

import me.randomhashtags.worldlaws.EventSource;
import me.randomhashtags.worldlaws.EventSources;
import me.randomhashtags.worldlaws.upcoming.UpcomingEventType;

public class NearEarthObjectEvent extends UpcomingEvent {
    private final String relativeVelocity;
    private final boolean potentiallyHazardous;
    private final long closeApproachEpoch;
    private final float estimatedDiameterMin, estimatedDiameterMax;

    public NearEarthObjectEvent(String name, long closeApproachEpoch, boolean potentiallyHazardous, float estimatedDiameterMin, float estimatedDiameterMax, String relativeVelocity) {
        super("NEO: " + name, "Near earth object description??", null, null, null, new EventSources(new EventSource("NASA", "https://cneos.jpl.nasa.gov")));
        this.closeApproachEpoch = closeApproachEpoch;
        this.potentiallyHazardous = potentiallyHazardous;
        this.estimatedDiameterMin = estimatedDiameterMin;
        this.estimatedDiameterMax = estimatedDiameterMax;
        this.relativeVelocity = relativeVelocity;
    }

    @Override
    public UpcomingEventType getType() {
        return UpcomingEventType.SPACE_NEAR_EARTH_OBJECT;
    }

    @Override
    public String getPropertiesJSONObject() {
        return "{" +
                "\"closeApproachEpoch\":" + closeApproachEpoch + "," +
                "\"potentiallyHazardous\":" + potentiallyHazardous + "," +
                "\"estimatedDiameterMin\":" + estimatedDiameterMin + "," +
                "\"estimatedDiameterMax\":" + estimatedDiameterMax + "," +
                "\"relativeVelocity\":" + relativeVelocity +
                "}";
    }

}
