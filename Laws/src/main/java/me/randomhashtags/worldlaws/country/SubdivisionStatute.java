package me.randomhashtags.worldlaws.country;

import me.randomhashtags.worldlaws.LocalServer;
import me.randomhashtags.worldlaws.locale.JSONObjectTranslatable;
import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

public final class SubdivisionStatute {
    private final StateReference reference;
    private String topic, description;
    private JSONArray json;

    private List<Subdivision> subdivisions;

    public SubdivisionStatute(StateReference reference, String topic, String description) {
        this(reference, topic, description, new ArrayList<>());
    }
    public SubdivisionStatute(StateReference reference, String topic, String description, List<Subdivision> subdivisions) {
        this.reference = reference;
        this.topic = topic;
        this.description = LocalServer.fixEscapeValues(description);
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
        this.description = LocalServer.fixEscapeValues(description);
    }

    public List<Subdivision> getSubdivisions() {
        return subdivisions;
    }
    public void setSubdivisions(List<Subdivision> subdivisions) {
        this.subdivisions = subdivisions;
    }

    private JSONArray getSubdivisionsJSON() {
        if(json == null) {
            json = new JSONArray();
            if(subdivisions != null) {
                for(Subdivision subdivision : subdivisions) {
                    json.put(subdivision.toJSONObject());
                }
            }
        }
        return json;
    }

    public JSONObjectTranslatable toJSONObject() {
        final JSONObjectTranslatable json = new JSONObjectTranslatable("topic", "reference", "subdivisions");
        json.put("topic", topic);
        json.put("reference", reference.toJSONObject());
        json.put("subdivisions", getSubdivisionsJSON());
        if(description != null) {
            json.put("description", description);
            json.addTranslatedKey("description");
        }
        return json;
    }
}
