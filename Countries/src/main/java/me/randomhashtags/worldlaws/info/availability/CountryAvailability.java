package me.randomhashtags.worldlaws.info.availability;

import me.randomhashtags.worldlaws.locale.JSONObjectTranslatable;

public final class CountryAvailability {
    private final AvailabilityCategory primaryCategory;
    private final String title, imageURL;
    private final boolean value;

    public CountryAvailability(String title, AvailabilityCategory primaryCategory, String imageURL, boolean value) {
        this.title = title;
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

    public JSONObjectTranslatable toJSONObject() {
        final JSONObjectTranslatable json = new JSONObjectTranslatable("title");
        json.put("title", title);
        if(imageURL != null) {
            json.put("imageURL", imageURL);
        }
        return json;
    }
}
