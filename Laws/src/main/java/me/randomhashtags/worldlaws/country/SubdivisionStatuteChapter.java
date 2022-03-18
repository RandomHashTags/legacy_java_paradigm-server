package me.randomhashtags.worldlaws.country;

import me.randomhashtags.worldlaws.locale.JSONObjectTranslatable;

public final class SubdivisionStatuteChapter implements SubdivisionLegal {
    private final String chapter;
    private String title;

    public SubdivisionStatuteChapter(String chapter) {
        this.chapter = chapter;
    }

    @Override
    public String getID() {
        return chapter;
    }

    @Override
    public String getTitle() {
        return title;
    }
    @Override
    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public JSONObjectTranslatable toJSONObject() {
        final JSONObjectTranslatable json = new JSONObjectTranslatable("title");
        json.put("title", title);
        return json;
    }
}
