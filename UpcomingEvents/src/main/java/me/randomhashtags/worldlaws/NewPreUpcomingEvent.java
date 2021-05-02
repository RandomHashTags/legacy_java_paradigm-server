package me.randomhashtags.worldlaws;

public final class NewPreUpcomingEvent {
    private final String id, title, url, tag;

    public NewPreUpcomingEvent(String id, String title, String url, String tag) {
        this.id = id;
        this.title = title;
        this.url = url;
        this.tag = tag;
    }

    public String getID() {
        return id;
    }
    public String getTitle() {
        return title;
    }
    public String getURL() {
        return url;
    }
    public String getTag() {
        return tag;
    }

    public PreUpcomingEvent getPreUpcomingEvent(String imageURL) {
        return new PreUpcomingEvent(id, title, tag, imageURL);
    }
}
