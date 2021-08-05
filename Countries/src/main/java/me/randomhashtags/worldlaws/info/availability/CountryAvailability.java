package me.randomhashtags.worldlaws.info.availability;

import me.randomhashtags.worldlaws.LocalServer;

public final class CountryAvailability {
    private final AvailabilityCategory primaryCategory;
    private final String title, imageURL;
    private final boolean value;

    public CountryAvailability(String title, AvailabilityCategory primaryCategory, String imageURL, boolean value) {
        this.title = LocalServer.fixEscapeValues(title);
        this.primaryCategory = primaryCategory;
        this.imageURL = imageURL;
        this.value = value;
    }

    public AvailabilityCategory getPrimaryCategory() {
        return primaryCategory;
    }
    public boolean isAvailable() {
        return value;
    }

    @Override
    public String toString() {
        return "\"" + title + "\":{" +
                (imageURL != null ? "\"imageURL\":\"" + imageURL + "\"" : "") +
                "}";
    }
}
