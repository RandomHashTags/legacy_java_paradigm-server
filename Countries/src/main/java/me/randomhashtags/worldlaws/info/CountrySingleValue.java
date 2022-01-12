package me.randomhashtags.worldlaws.info;

import me.randomhashtags.worldlaws.EventSources;
import me.randomhashtags.worldlaws.LocalServer;
import org.json.JSONObject;

public final class CountrySingleValue {
    private final String title;
    private final String notes, value, valueDescription;
    private final int yearOfData;
    private final EventSources sources;

    public CountrySingleValue(String title, String notes, String value, String valueDescription, int yearOfData, EventSources sources) {
        this.title = LocalServer.fixEscapeValues(title);
        this.notes = LocalServer.fixEscapeValues(notes);
        this.value = LocalServer.fixEscapeValues(value);
        this.valueDescription = LocalServer.fixEscapeValues(valueDescription);
        this.yearOfData = yearOfData;
        this.sources = sources;
    }

    public JSONObject toJSONObject() {
        final JSONObject json = new JSONObject();
        final JSONObject detailsJSON = new JSONObject();
        if(notes != null) {
            detailsJSON.put("notes", notes);
        }
        detailsJSON.put("value", value);
        if(valueDescription != null && !valueDescription.isEmpty()) {
            detailsJSON.put("valueDescription", valueDescription);
        }
        if(yearOfData != -1) {
            detailsJSON.put("yearOfData", yearOfData);
        }
        detailsJSON.put("sources", sources.toJSONObject());
        json.put(title, detailsJSON);
        return json;
    }
}
