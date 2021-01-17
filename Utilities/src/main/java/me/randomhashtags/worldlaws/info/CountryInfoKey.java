package me.randomhashtags.worldlaws.info;

import me.randomhashtags.worldlaws.EventSources;
import me.randomhashtags.worldlaws.LocalServer;

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
                "\"notes\":\"" + notes + "\"," +
                "\"yearOfData\":" + yearOfData + "," +
                "\"sources\":" + sources.toString() + "," +
                "\"values\":" + getValuesJSON() +
                "}";
    }
}
