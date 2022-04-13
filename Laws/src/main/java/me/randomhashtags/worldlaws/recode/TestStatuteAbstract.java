package me.randomhashtags.worldlaws.recode;

public class TestStatuteAbstract {
    protected final String id, title;

    public TestStatuteAbstract(String id, String title) {
        this.id = id;
        this.title = title;
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
