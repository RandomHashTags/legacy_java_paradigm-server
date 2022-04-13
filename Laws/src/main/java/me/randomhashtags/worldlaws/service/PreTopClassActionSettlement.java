package me.randomhashtags.worldlaws.service;

import me.randomhashtags.worldlaws.locale.JSONObjectTranslatable;

public final class PreTopClassActionSettlement {
    private final String title, description, potentialSettlement, imageURL, url;

    public PreTopClassActionSettlement(String title, String description, String potentialSettlement, String imageURL, String url) {
        this.title = title;
        this.description = description;
        this.imageURL = imageURL;
        this.potentialSettlement = potentialSettlement;
        this.url = url;
    }

    public String getURL() {
        return url;
    }

    public String getTitle() {
        return title;
    }

    public JSONObjectTranslatable toJSONObject() {
        final JSONObjectTranslatable json = new JSONObjectTranslatable("description", "potentialSettlement");
        json.put("description", description);
        json.put("potentialSettlement", potentialSettlement);
        json.put("imageURL", imageURL);
        return json;
    }
}
