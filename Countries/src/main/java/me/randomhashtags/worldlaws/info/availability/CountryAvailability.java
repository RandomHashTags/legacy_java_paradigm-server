package me.randomhashtags.worldlaws.info.availability;

import me.randomhashtags.worldlaws.LocalServer;

public final class CountryAvailability {
    private final String title, category;
    private final boolean value;

    public CountryAvailability(String title, boolean value, CountryAvailabilityCategory category) {
        this.title = LocalServer.fixEscapeValues(title);
        this.value = value;
        this.category = category.name();
    }

    @Override
    public String toString() {
        return "{" +
                "\"title\":\"" + title + "\"," +
                "\"value\":" + value + "," +
                "\"category\":\"" + category + "\"" +
                "}";
    }
}
