package me.randomhashtags.worldlaws.info;

import me.randomhashtags.worldlaws.locale.JSONObjectTranslatable;
import org.json.JSONObject;

public final class CountrySingleValue {

    public static CountrySingleValue parse(JSONObject json) {
        return new CountrySingleValue(json);
    }

    private final String notes, value, valueDescription;
    private final int yearOfData;

    public CountrySingleValue(String notes, String value, String valueDescription, int yearOfData) {
        this.notes = notes;
        this.value = value;
        this.valueDescription = valueDescription;
        this.yearOfData = yearOfData;
    }
    private CountrySingleValue(JSONObject json) {
        notes = json.optString("notes", null);
        value = json.optString("value", null);
        valueDescription = json.optString("valueDescription", null);
        yearOfData = json.optInt("yearOfData", -1);
    }

    public JSONObjectTranslatable toJSONObject() {
        final JSONObjectTranslatable json = new JSONObjectTranslatable();
        if(notes != null) {
            json.put("notes", notes);
            json.addTranslatedKey("notes");
        }
        json.put("value", value);
        if(value != null && !value.startsWith("https")) {
            json.addTranslatedKey("value");
        }
        if(valueDescription != null && !valueDescription.isEmpty()) {
            json.put("valueDescription", valueDescription);
            json.addTranslatedKey("valueDescription");
        }
        if(yearOfData != -1) {
            json.put("yearOfData", yearOfData);
        }
        return json;
    }
}
