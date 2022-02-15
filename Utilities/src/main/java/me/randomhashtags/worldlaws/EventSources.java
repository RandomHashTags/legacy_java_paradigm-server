package me.randomhashtags.worldlaws;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;

public final class EventSources extends ArrayList<EventSource> {

    public EventSources(EventSource...sources) {
        addAll(Arrays.asList(sources));
    }
    public EventSources(EventSources eventSources) {
        addAll(eventSources);
    }

    public JSONObject toJSONObject() {
        final JSONObject json = new JSONObject();
        for(EventSource source : this) {
            json.put(source.getSiteName(), source.getJSON());
        }
        return json;
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder("{");
        boolean isFirst = true;
        for(EventSource source : this) {
            builder.append(isFirst ? "" : ",").append(source.toString());
            isFirst = false;
        }
        builder.append("}");
        return builder.toString();
    }
}
