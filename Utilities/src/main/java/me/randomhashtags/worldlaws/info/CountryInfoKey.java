package me.randomhashtags.worldlaws.info;

import me.randomhashtags.worldlaws.EventSources;
import me.randomhashtags.worldlaws.LocalServer;
import me.randomhashtags.worldlaws.ServerObject;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class CountryInfoKey implements ServerObject {
    public String country;
    private String title;
    private final String notes;
    private int yearOfData;
    private EventSources sources;
    private final List<CountryInfoValue> values;

    public CountryInfoKey(String notes, int yearOfData, CountryInfoValue...values) {
        this.notes = LocalServer.fixEscapeValues(notes);
        this.yearOfData = yearOfData;
        this.values = new ArrayList<>(Arrays.asList(values));
    }
    public CountryInfoKey(JSONObject json) {
        notes = LocalServer.fixEscapeValues(json.has("notes") ? json.getString("notes") : null);
        yearOfData = json.has("yearOfData") ? json.getInt("yearOfData") : -1;
        values = new ArrayList<>();
        for(Object obj : json.getJSONArray("values")) {
            final JSONObject valueJSON = (JSONObject) obj;
            final CountryInfoValue value = new CountryInfoValue(valueJSON);
            values.add(value);
        }
    }

    public void setTitle(String title) {
        this.title = LocalServer.fixEscapeValues(title);
    }
    public void setYearOfData(int yearOfData) {
        this.yearOfData = yearOfData;
    }
    public void setSources(EventSources sources) {
        this.sources = sources;
    }

    public void addValue(CountryInfoValue value) {
        values.add(value);
    }
    private String getValuesJSON() {
        final StringBuilder builder = new StringBuilder("[");
        boolean isFirst = true;
        for(CountryInfoValue value : values) {
            if(value != null) {
                builder.append(isFirst ? "" : ",").append(value.toString());
                isFirst = false;
            }
        }
        builder.append("]");
        return builder.toString();
    }

    @Override
    public String toString() {
        return "{" +
                "\"title\":\"" + title + "\"," +
                (notes != null && !notes.isEmpty() ? "\"notes\":\"" + notes + "\"," : "") +
                (yearOfData != -1 ? "\"yearOfData\":" + yearOfData + "," : "") +
                "\"sources\":" + sources.toString() + "," +
                "\"values\":" + getValuesJSON() +
                "}";
    }

    @Override
    public String toServerJSON() {
        return "{" +
                "\"country\":\"" + country + "\"," +
                (notes != null && !notes.isEmpty() ? "\"notes\":\"" + notes + "\"," : "") +
                (yearOfData != -1 ? "\"yearOfData\":" + yearOfData + "," : "") +
                "\"values\":" + getValuesJSON() +
                "}";
    }
}
