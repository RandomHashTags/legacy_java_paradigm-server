package me.randomhashtags.worldlaws.recode;

import me.randomhashtags.worldlaws.LocalServer;

public class TestStatuteAbstract {
    protected final String id, title;

    public TestStatuteAbstract(String id, String title) {
        this.id = id;
        this.title = LocalServer.fixEscapeValues(title);
    }

    public String getID() {
        return id;
    }
    public String getTitle() {
        return title;
    }

    @Override
    public String toString() {
        return "\"" + id + "\":{" +
                "\"title\":\"" + title + "\"" +
                "}";
    }
}
