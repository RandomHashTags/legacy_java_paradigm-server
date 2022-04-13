package me.randomhashtags.worldlaws.info;

import me.randomhashtags.worldlaws.LocalServer;
import me.randomhashtags.worldlaws.locale.JSONArrayTranslatable;
import me.randomhashtags.worldlaws.locale.JSONObjectTranslatable;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class CountryInfoKey {

    public static CountryInfoKey parse(JSONObject json) {
        return new CountryInfoKey(json);
    }

    private final String notes;
    private final int yearOfData;
    private final List<CountryInfoValue> values;

    public CountryInfoKey(String notes, int yearOfData, CountryInfoValue...values) {
        this.notes = LocalServer.removeWikipediaReferences(notes);
        this.yearOfData = yearOfData;
        this.values = new ArrayList<>(Arrays.asList(values));
    }
    private CountryInfoKey(JSONObject json) {
        notes = json.optString("notes", null);
        yearOfData = json.optInt("yearOfData", -1);
        values = new ArrayList<>();
        if(json.has("values")) {
            final JSONArray array = json.getJSONArray("values");
            for(Object obj : array) {
                final JSONObject valueJSON = (JSONObject) obj;
                final CountryInfoValue infoValue = CountryInfoValue.parse(valueJSON);
                values.add(infoValue);
            }
        }
    }

    private JSONArrayTranslatable getValuesJSONObject() {
        final JSONArrayTranslatable array = new JSONArrayTranslatable();
        for(CountryInfoValue value : values) {
            if(value != null) {
                array.put(value.toJSONObject());
            }
        }
        return array;
    }

    public JSONObjectTranslatable toJSONObject() {
        final JSONObjectTranslatable json = new JSONObjectTranslatable("values");
        if(notes != null && !notes.isEmpty()) {
            json.put("notes", notes);
            json.addTranslatedKey("notes");
        }
        if(yearOfData != -1) {
            json.put("yearOfData", yearOfData);
        }
        json.put("values", getValuesJSONObject());
        return json;
    }
}
