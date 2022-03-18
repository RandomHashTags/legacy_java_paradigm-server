package me.randomhashtags.worldlaws.country;

import me.randomhashtags.worldlaws.locale.JSONObjectTranslatable;

public final class SubdivisionStatuteIndex implements SubdivisionLegal {
    private final String backendID;
    private String title;

    public SubdivisionStatuteIndex(String backendID) {
        this.backendID = backendID;
    }

    @Override
    public String getID() {
        return backendID;
    }
    @Override
    public String getTitle() {
        return title;
    }
    @Override
    public void setTitle(String title) {
        this.title = title;
    }

    public String getBackendID() {
        return backendID;
    }

    @Override
    public JSONObjectTranslatable toJSONObject() {
        final JSONObjectTranslatable json = new JSONObjectTranslatable("title");
        json.put("title", title);
        return json;
    }
}
