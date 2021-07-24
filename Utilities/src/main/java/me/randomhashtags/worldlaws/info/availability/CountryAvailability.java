package me.randomhashtags.worldlaws.info.availability;

import me.randomhashtags.worldlaws.LocalServer;

public final class CountryAvailability {
    public String country;
    private final AvailabilityCategory primaryCategory;
    private final String title, imageURL;
    private final boolean value;

    public CountryAvailability(String title, AvailabilityCategory primaryCategory, String imageURL, boolean value) {
        this.title = LocalServer.fixEscapeValues(title);
        this.primaryCategory = primaryCategory;
        this.imageURL = imageURL;
        this.value = value;
    }

    @Override
    public String toString() {
        return "\"" + title + "\":{" +
                (primaryCategory != null ? "\"primaryCategory\":\"" + primaryCategory.name() + "\"," : "") +
                (imageURL != null ? "\"imageURL\":\"" + imageURL + "\"," : "") +
                "\"value\":" + value +
                "}";
    }
}
