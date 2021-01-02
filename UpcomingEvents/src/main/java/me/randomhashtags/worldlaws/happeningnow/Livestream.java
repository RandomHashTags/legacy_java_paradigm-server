package me.randomhashtags.worldlaws.happeningnow;

import me.randomhashtags.worldlaws.LocalServer;

public final class Livestream {
    private final String streamerName, title, thumbnailURL;
    private final int viewers;
    private final LivestreamType type;

    public Livestream(String streamerName, String title, String thumbnailURL, int viewers, LivestreamType type) {
        this.streamerName = streamerName;
        this.title = LocalServer.fixEscapeValues(title);
        this.thumbnailURL = thumbnailURL;
        this.viewers = viewers;
        this.type = type;
    }

    public String getStreamerName() {
        return streamerName;
    }
    public String getTitle() {
        return title;
    }
    public String getThumbnailURL() {
        return thumbnailURL;
    }
    public int getViewers() {
        return viewers;
    }
    public LivestreamType getType() {
        return type;
    }

    @Override
    public String toString() {
        return "{" +
                "\"streamerName\":\"" + streamerName + "\"," +
                "\"title\":\"" + title + "\"," +
                "\"thumbnailURL\":\"" + thumbnailURL + "\"," +
                "\"viewers\":" + viewers + "," +
                "\"type\":\"" + type.name() + "\"" +
                "}";
    }
}
