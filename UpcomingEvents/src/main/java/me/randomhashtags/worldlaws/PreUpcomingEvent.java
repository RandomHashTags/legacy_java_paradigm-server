package me.randomhashtags.worldlaws;

public final class PreUpcomingEvent {
    private final String title, tag, imageURL;

    public PreUpcomingEvent(String title, String tag, String imageURL) {
        this.title = LocalServer.fixEscapeValues(title);
        this.tag = LocalServer.fixEscapeValues(tag);
        this.imageURL = LocalServer.fixEscapeValues(imageURL);
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
                "\"title\":\"" + title + "\"," +
                (imageURL != null ? "\"imageURL\":\"" + imageURL + "\"," : "") +
                "\"tag\":\"" + tag + "\"" +
                "}";
    }
}
