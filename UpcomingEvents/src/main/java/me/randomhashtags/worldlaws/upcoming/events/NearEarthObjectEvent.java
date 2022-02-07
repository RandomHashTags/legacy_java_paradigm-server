package me.randomhashtags.worldlaws.upcoming.events;

import me.randomhashtags.worldlaws.EventSources;
import me.randomhashtags.worldlaws.upcoming.UpcomingEventType;
import me.randomhashtags.worldlaws.upcoming.UpcomingEventValue;

public class NearEarthObjectEvent extends UpcomingEvent {
    private final String relativeVelocity;
    private final boolean potentiallyHazardous;
    private final long closeApproachEpoch;
    private final float estimatedDiameterMin, estimatedDiameterMax;

    public NearEarthObjectEvent(String name, long closeApproachEpoch, boolean potentiallyHazardous, float estimatedDiameterMin, float estimatedDiameterMax, String relativeVelocity, EventSources sources) {
        super("NEO: " + name, "Near earth object description??", null, null, null, sources);
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
                "\"" + UpcomingEventValue.NEAR_EARTH_OBJECT_CLOSE_APPROACH_EPOCH.getKey() + "\":" + closeApproachEpoch + "," +
                "\"" + UpcomingEventValue.NEAR_EARTH_OBJECT_POTENTIALLY_HAZARDOUS.getKey() + "\":" + potentiallyHazardous + "," +
                "\"" + UpcomingEventValue.NEAR_EARTH_OBJECT_ESTIMATED_DIAMETER_MIN.getKey() + "\":" + estimatedDiameterMin + "," +
                "\"" + UpcomingEventValue.NEAR_EARTH_OBJECT_ESTIMATED_DIAMETER_MAX.getKey() + "\":" + estimatedDiameterMax + "," +
                "\"" + UpcomingEventValue.NEAR_EARTH_OBJECT_RELATIVE_VELOCITY.getKey() + "\":" + relativeVelocity +
                "}";
    }

}
