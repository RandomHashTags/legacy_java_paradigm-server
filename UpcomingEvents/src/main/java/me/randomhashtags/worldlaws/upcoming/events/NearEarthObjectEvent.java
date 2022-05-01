package me.randomhashtags.worldlaws.upcoming.events;

import me.randomhashtags.worldlaws.EventDate;
import me.randomhashtags.worldlaws.EventSources;
import me.randomhashtags.worldlaws.locale.JSONObjectTranslatable;
import me.randomhashtags.worldlaws.upcoming.UpcomingEventType;
import me.randomhashtags.worldlaws.upcoming.UpcomingEventValue;
import org.json.JSONObject;

public class NearEarthObjectEvent extends UpcomingEvent {
    private final String relativeVelocity;
    private final boolean potentiallyHazardous;
    private final long closeApproachEpoch;
    private final float estimatedDiameterMin, estimatedDiameterMax;

    public NearEarthObjectEvent(JSONObject json) {
        super(json);
        final JSONObject properties = json.getJSONObject("properties");
        closeApproachEpoch = properties.getLong(UpcomingEventValue.NEAR_EARTH_OBJECT_CLOSE_APPROACH_EPOCH.getKey());
        potentiallyHazardous = properties.getBoolean(UpcomingEventValue.NEAR_EARTH_OBJECT_POTENTIALLY_HAZARDOUS.getKey());
        estimatedDiameterMin = properties.getFloat(UpcomingEventValue.NEAR_EARTH_OBJECT_ESTIMATED_DIAMETER_MIN.getKey());
        estimatedDiameterMax = properties.getFloat(UpcomingEventValue.NEAR_EARTH_OBJECT_ESTIMATED_DIAMETER_MAX.getKey());
        relativeVelocity = properties.getString(UpcomingEventValue.NEAR_EARTH_OBJECT_RELATIVE_VELOCITY.getKey());
    }
    public NearEarthObjectEvent(EventDate date, String name, long closeApproachEpoch, boolean potentiallyHazardous, float estimatedDiameterMin, float estimatedDiameterMax, String relativeVelocity, EventSources sources) {
        super(date, "NEO: " + name, "Near earth object description??", null, null, null, sources);
        this.closeApproachEpoch = closeApproachEpoch;
        this.potentiallyHazardous = potentiallyHazardous;
        this.estimatedDiameterMin = estimatedDiameterMin;
        this.estimatedDiameterMax = estimatedDiameterMax;
        this.relativeVelocity = relativeVelocity;
        insertProperties();
    }

    @Override
    public UpcomingEventType getType() {
        return UpcomingEventType.SPACE_NEAR_EARTH_OBJECT;
    }

    @Override
    public JSONObjectTranslatable getPropertiesJSONObject() {
        final JSONObjectTranslatable json = new JSONObjectTranslatable();
        json.put(UpcomingEventValue.NEAR_EARTH_OBJECT_CLOSE_APPROACH_EPOCH.getKey(), closeApproachEpoch);
        json.put(UpcomingEventValue.NEAR_EARTH_OBJECT_POTENTIALLY_HAZARDOUS.getKey(), potentiallyHazardous);
        json.put(UpcomingEventValue.NEAR_EARTH_OBJECT_ESTIMATED_DIAMETER_MIN.getKey(), estimatedDiameterMin);
        json.put(UpcomingEventValue.NEAR_EARTH_OBJECT_ESTIMATED_DIAMETER_MAX.getKey(), estimatedDiameterMax);
        json.put(UpcomingEventValue.NEAR_EARTH_OBJECT_RELATIVE_VELOCITY.getKey(), relativeVelocity);
        return json;
    }
}
