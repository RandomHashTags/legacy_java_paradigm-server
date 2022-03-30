package me.randomhashtags.worldlaws;

import me.randomhashtags.worldlaws.locale.JSONObjectTranslatable;
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
    public EventSources(JSONObject json) {
        for(String siteName : json.keySet()) {
            final JSONObject sourceJSON = json.getJSONObject(siteName);
            final EventSource source = new EventSource(siteName, sourceJSON);
            add(source);
        }
    }

    public JSONObjectTranslatable toJSONObject() {
        final JSONObjectTranslatable json = new JSONObjectTranslatable();
        for(EventSource source : this) {
            json.put(source.getSiteName(), source.toJSONObject(false));
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
