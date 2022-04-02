package me.randomhashtags.worldlaws.upcoming.events;

import me.randomhashtags.worldlaws.locale.JSONObjectTranslatable;

public final class LoadedPreUpcomingEvent {

    private final String identifier;
    private final JSONObjectTranslatable json;

    public LoadedPreUpcomingEvent(String identifier, JSONObjectTranslatable json) {
        this.identifier = identifier;
        this.json = json;
    }

    public String getIdentifier() {
        return identifier;
    }
    public String getDateString() {
        return identifier.split("\\.")[0];
    }
    public String getID() {
        return identifier.substring(getDateString().length()+1);
    }
    public JSONObjectTranslatable getJSONObject() {
        return json;
    }
}
