package me.randomhashtags.worldlaws.info;

import me.randomhashtags.worldlaws.EventSources;
import me.randomhashtags.worldlaws.LocalServer;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class CountryInfoKey {
    private final String title, notes;
    private final int yearOfData;
    private final EventSources sources;
    private final List<CountryInfoValue> values;

    public CountryInfoKey(String title, String notes, int yearOfData, EventSources sources, CountryInfoValue...values) {
        this.title = LocalServer.fixEscapeValues(title);
        this.notes = LocalServer.fixEscapeValues(notes);
        this.yearOfData = yearOfData;
        this.sources = sources;
        this.values = new ArrayList<>(Arrays.asList(values));
    }

    public void addValue(CountryInfoValue value) {
        values.add(value);
    }
    private JSONObject getValuesJSONObject() {
        final JSONObject json = new JSONObject();
        for(CountryInfoValue value : values) {
            if(value != null) {
                json.put(value.getTitle(), value.toJSONObject());
            }
        }
        return json;
    }

    public JSONObject toJSONObject() {
        final JSONObject json = new JSONObject();
        final JSONObject detailsJSON = new JSONObject();
        if(notes != null && !notes.isEmpty()) {
            detailsJSON.put("notes", notes);
        }
        if(yearOfData != -1) {
            detailsJSON.put("yearOfData", yearOfData);
        }
        detailsJSON.put("sources", sources.toJSONObject());
        detailsJSON.put("values", getValuesJSONObject());
        json.put(title, detailsJSON);
        return json;
    }
}
