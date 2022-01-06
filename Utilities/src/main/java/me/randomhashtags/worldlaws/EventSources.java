package me.randomhashtags.worldlaws;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class EventSources {
    private final List<EventSource> sources;

    public EventSources(EventSource...sources) {
        this.sources = new ArrayList<>();
        this.sources.addAll(Arrays.asList(sources));
    }

    public List<EventSource> getSources() {
        return sources;
    }
    public void append(EventSources sources) {
        this.sources.addAll(sources.getSources());
    }
    public void append(@NotNull EventSource source) {
        sources.add(source);
    }

    public JSONObject toJSONObject() {
        final JSONObject json = new JSONObject();
        for(EventSource source : sources) {
            json.put(source.getSiteName(), source.getJSON());
        }
        return json;
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder("{");
        boolean isFirst = true;
        for(EventSource source : sources) {
            builder.append(isFirst ? "" : ",").append(source.toString());
            isFirst = false;
        }
        builder.append("}");
        return builder.toString();
    }
}
