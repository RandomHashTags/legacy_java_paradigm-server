package me.randomhashtags.worldlaws.country;

import me.randomhashtags.worldlaws.locale.JSONObjectTranslatable;

public final class StateReference {
    private final String title, chapter, section, url;

    public StateReference(String title, String chapter, String section, String url) {
        this.title = title;
        this.chapter = chapter;
        this.section = section;
        this.url = url;
    }

    public String getTitle() {
        return title;
    }
    public String getChapter() {
        return chapter;
    }
    public String getSection() {
        return section;
    }
    public String getURL() {
        return url;
    }

    public static StateReference build(String title, String chapter, String section, String url) {
        return new StateReference(title, chapter, section, url != null ? url : "https://www.ecosia.org");
    }

    @Override
    public String toString() {
        return "{" +
                "\"title\":\"" + title + "\"," +
                "\"chapter\":\"" + chapter + "\"," +
                "\"section\":\"" + section + "\"," +
                "\"url\":\"" + url + "\"" +
                "}";
    }

    public JSONObjectTranslatable toJSONObject() {
        final JSONObjectTranslatable json = new JSONObjectTranslatable("title", "chapter");
        json.put("title", title);
        json.put("chapter", chapter);
        json.put("section", section);
        json.put("url", url);
        return json;
    }
}
