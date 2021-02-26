package me.randomhashtags.worldlaws.info;

import me.randomhashtags.worldlaws.EventSources;
import me.randomhashtags.worldlaws.LocalServer;

public final class CountrySingleValue {
    private final String title, notes, value, valueDescription;
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

    @Override
    public String toString() {
        return "{" +
                "\"title\":\"" + title + "\"," +
                (notes != null && !notes.isEmpty() ? "\"notes\":\"" + notes + "\"," : "") +
                "\"value\":\"" + value + "\"," +
                (valueDescription != null && !valueDescription.isEmpty() ? "\"valueDescription\":\"" + valueDescription + "\"," : "") +
                "\"yearOfData\":" + yearOfData + "," +
                "\"sources\":" + sources.toString() + "," +
                "}";
    }
}
