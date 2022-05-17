package me.randomhashtags.worldlaws.upcoming.events;

import me.randomhashtags.worldlaws.EventSources;
import me.randomhashtags.worldlaws.locale.JSONObjectTranslatable;
import me.randomhashtags.worldlaws.upcoming.UpcomingEventType;
import me.randomhashtags.worldlaws.upcoming.UpcomingEventValue;
import org.json.JSONObject;

public final class LunarEclipseEvent extends UpcomingEvent {

    private final long timeGreatestMillseconds;
    private final String orbitalNode, saros, gamma, magnitudePenumbra, magnitudeUmbra;
    private final int durationPartial, durationTotal;

    public LunarEclipseEvent(JSONObject json) {
        super(json);
        final JSONObject propertiesJSON = json.getJSONObject("properties");
        timeGreatestMillseconds = propertiesJSON.getLong(UpcomingEventValue.SPACE_LUNAR_ECLIPSE_TIME_GREATEST_MILLISECONDS.getKey());
        orbitalNode = propertiesJSON.getString(UpcomingEventValue.SPACE_LUNAR_ECLIPSE_ORBITAL_NODE.getKey());
        saros = propertiesJSON.getString(UpcomingEventValue.SPACE_LUNAR_ECLIPSE_SAROS.getKey());
        gamma = propertiesJSON.getString(UpcomingEventValue.SPACE_LUNAR_ECLIPSE_GAMMA.getKey());
        magnitudePenumbra = propertiesJSON.getString(UpcomingEventValue.SPACE_LUNAR_ECLIPSE_MAGNITUDE_PENUMBRA.getKey());
        magnitudeUmbra = propertiesJSON.getString(UpcomingEventValue.SPACE_LUNAR_ECLIPSE_MAGNITUDE_UMBRA.getKey());
        durationPartial = propertiesJSON.optInt(UpcomingEventValue.SPACE_LUNAR_ECLIPSE_DURATION_PARTIAL.getKey(), 0);
        durationTotal = propertiesJSON.optInt(UpcomingEventValue.SPACE_LUNAR_ECLIPSE_DURATION_TOTAL.getKey(), 0);
    }
    public LunarEclipseEvent(long exactTimeMilliseconds, String title, String description, String imageURL, long timeGreatestMillseconds, String orbitalNode, String saros, String gamma, String magnitudePenumbra, String magnitudeUmbra, int durationPartial, int durationTotal, EventSources sources) {
        super(exactTimeMilliseconds, title, description, imageURL, null, null, sources);
        this.timeGreatestMillseconds = timeGreatestMillseconds;
        this.orbitalNode = orbitalNode;
        this.saros = saros;
        this.gamma = gamma;
        this.magnitudePenumbra = magnitudePenumbra;
        this.magnitudeUmbra = magnitudeUmbra;
        this.durationPartial = durationPartial;
        this.durationTotal = durationTotal;
        insertProperties();
    }

    @Override
    public UpcomingEventType getType() {
        return UpcomingEventType.SPACE_LUNAR_ECLIPSE;
    }

    @Override
    public JSONObjectTranslatable getPropertiesJSONObject() {
        final String orbitalNodeKey = UpcomingEventValue.SPACE_LUNAR_ECLIPSE_ORBITAL_NODE.getKey();
        final String sarosKey = UpcomingEventValue.SPACE_LUNAR_ECLIPSE_SAROS.getKey();
        final String gammaKey = UpcomingEventValue.SPACE_LUNAR_ECLIPSE_GAMMA.getKey();
        final String magnitudePenumbraKey = UpcomingEventValue.SPACE_LUNAR_ECLIPSE_MAGNITUDE_PENUMBRA.getKey();
        final String magnitudeUmbraKey = UpcomingEventValue.SPACE_LUNAR_ECLIPSE_MAGNITUDE_UMBRA.getKey();
        final JSONObjectTranslatable json = new JSONObjectTranslatable(orbitalNodeKey, sarosKey, gammaKey, magnitudePenumbraKey, magnitudeUmbraKey);
        json.put(UpcomingEventValue.SPACE_LUNAR_ECLIPSE_TIME_GREATEST_MILLISECONDS.getKey(), timeGreatestMillseconds);
        json.put(orbitalNodeKey, orbitalNode);
        json.put(sarosKey, saros);
        json.put(gammaKey, gamma);
        json.put(magnitudePenumbraKey, magnitudePenumbra);
        json.put(magnitudeUmbraKey, magnitudeUmbra);
        if(durationPartial > 0) {
            json.put(UpcomingEventValue.SPACE_LUNAR_ECLIPSE_DURATION_PARTIAL.getKey(), durationPartial);
        }
        if(durationTotal > 0) {
            json.put(UpcomingEventValue.SPACE_LUNAR_ECLIPSE_DURATION_TOTAL.getKey(), durationTotal);
        }
        return json;
    }
}
