package me.randomhashtags.worldlaws.country;

import me.randomhashtags.worldlaws.LocalServer;

import java.util.ArrayList;
import java.util.List;

public final class SubdivisionStatute {
    private final StateReference reference;
    private String topic, description, json;

    private List<Subdivision> subdivisions;

    public SubdivisionStatute(StateReference reference, String topic, String description) {
        this(reference, topic, description, new ArrayList<>());
    }
    public SubdivisionStatute(StateReference reference, String topic, String description, List<Subdivision> subdivisions) {
        this.reference = reference;
        this.topic = topic;
        this.description = description;
        this.subdivisions = subdivisions;
    }

    public StateReference getReference() {
        return reference;
    }
    public String getTopic() {
        return topic;
    }
    public void setTopic(String topic) {
        this.topic = topic;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }

    public List<Subdivision> getSubdivisions() {
        return subdivisions;
    }
    public void setSubdivisions(List<Subdivision> subdivisions) {
        this.subdivisions = subdivisions;
    }

    private String getSubdivisionsJSON() {
        if(json == null) {
            final StringBuilder builder = new StringBuilder("[");
            if(subdivisions != null) {
                int index = 0, max = subdivisions.size()-1;
                for(Subdivision subdivision : subdivisions) {
                    builder.append(subdivision.toString()).append(index == max ? "" : ",");
                    index++;
                }
            }
            builder.append("]");
            json = builder.toString();
        }
        return json;
    }

    @Override
    public String toString() {
        return "{" +
                "\"reference\":" + reference.toString() + "," +
                "\"topic\":\"" + topic + "\"," +
                (description != null ? "\"description\":\"" + LocalServer.fixEscapeValues(description) + "\"," : "") +
                "\"subdivisions\":" + getSubdivisionsJSON() +
                "}";
    }
}
