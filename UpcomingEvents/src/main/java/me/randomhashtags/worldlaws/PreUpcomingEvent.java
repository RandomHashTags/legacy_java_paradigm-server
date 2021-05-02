package me.randomhashtags.worldlaws;

public final class PreUpcomingEvent {
    private final String id, title, tag, imageURL;

    public PreUpcomingEvent(String id, String title, String tag, String imageURL) {
        this.id = id.replace("|", "");
        this.title = LocalServer.fixEscapeValues(title);
        this.tag = LocalServer.fixEscapeValues(tag);
        this.imageURL = LocalServer.fixEscapeValues(imageURL);
    }

    public String getID() {
        return id;
    }
    public String getTitle() {
        return title;
    }
    public String getTag() {
        return tag;
    }
    public String getImageURL() {
        return imageURL;
    }

    @Override
    public String toString() {
        return "{" +
                "\"id\":\"" + id + "\"," +
                "\"title\":\"" + title + "\"," +
                (imageURL != null ? "\"imageURL\":\"" + imageURL + "\"," : "") +
                "\"tag\":\"" + tag + "\"" +
                "}";
    }
}
