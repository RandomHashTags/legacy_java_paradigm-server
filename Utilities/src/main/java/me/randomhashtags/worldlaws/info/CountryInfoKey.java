package me.randomhashtags.worldlaws.info;

import me.randomhashtags.worldlaws.EventSources;
import me.randomhashtags.worldlaws.LocalServer;

public final class CountryInfoKey {
    private final String title, notes;
    private final CountryInfoValue[] values;
    private final EventSources sources;

    public CountryInfoKey(String title, String notes, EventSources sources, CountryInfoValue...values) {
        this.title = LocalServer.fixEscapeValues(title);
        this.notes = LocalServer.fixEscapeValues(notes);
        this.sources = sources;
        this.values = values;
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
                "\"sources\":" + sources.toString() + "," +
                "\"values\":" + getValuesJSON() +
                "}";
    }
}
