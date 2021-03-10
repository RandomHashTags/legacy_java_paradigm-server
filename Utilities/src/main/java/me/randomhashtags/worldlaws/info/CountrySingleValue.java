package me.randomhashtags.worldlaws.info;

import me.randomhashtags.worldlaws.EventSources;
import me.randomhashtags.worldlaws.LocalServer;
import me.randomhashtags.worldlaws.ServerObject;
import org.json.JSONObject;

public final class CountrySingleValue implements ServerObject {
    public String country;
    private String title;
    private final String notes, value, valueDescription;
    private int yearOfData;
    private EventSources sources;

    public CountrySingleValue(String notes, String value, String valueDescription, int yearOfData) {
        this.notes = LocalServer.fixEscapeValues(notes);
        this.value = LocalServer.fixEscapeValues(value);
        this.valueDescription = LocalServer.fixEscapeValues(valueDescription);
        this.yearOfData = yearOfData;
    }
    public CountrySingleValue(JSONObject json) {
        this.notes = LocalServer.fixEscapeValues(json.has("notes") ? json.getString("notes") : null);
        this.value = LocalServer.fixEscapeValues(json.getString("value"));
        this.valueDescription = LocalServer.fixEscapeValues(json.has("valueDescription") ? json.getString("valueDescription") : null);
        this.yearOfData = json.has("yearOfData") ? json.getInt("yearOfData") : -1;
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

    @Override
    public String toString() {
        return "{" +
                "\"title\":\"" + title + "\"," +
                (notes != null && !notes.isEmpty() ? "\"notes\":\"" + notes + "\"," : "") +
                "\"value\":\"" + value + "\"," +
                (valueDescription != null && !valueDescription.isEmpty() ? "\"valueDescription\":\"" + valueDescription + "\"," : "") +
                (yearOfData != -1 ? "\"yearOfData\":" + yearOfData + "," : "") +
                "\"sources\":" + sources.toString() +
                "}";
    }

    @Override
    public String toServerJSON() {
        return "{" +
                "\"country\":\"" + country + "\"," +
                (notes != null && !notes.isEmpty() ? "\"notes\":\"" + notes + "\"," : "") +
                (valueDescription != null && !valueDescription.isEmpty() ? "\"valueDescription\":\"" + valueDescription + "\"," : "") +
                (yearOfData != -1 ? "\"yearOfData\":" + yearOfData + "," : "") +
                "\"value\":\"" + value + "\"" +
                "}";
    }
}
