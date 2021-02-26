package me.randomhashtags.worldlaws.info;

import me.randomhashtags.worldlaws.LocalServer;

public class CountryInfoValue {
    private final String title, value, description;

    public CountryInfoValue(String title, String value, String description) {
        this.title = LocalServer.fixEscapeValues(title);
        this.value = LocalServer.fixEscapeValues(value);
        this.description = LocalServer.fixEscapeValues(description);
    }

    @Override
    public String toString() {
        return "{" +
                (description != null ? "\"description\":\"" + description + "\"," : "") +
                "\"title\":\"" + title + "\"," +
                "\"value\":\"" + value + "\"" +
                "}";
    }
}