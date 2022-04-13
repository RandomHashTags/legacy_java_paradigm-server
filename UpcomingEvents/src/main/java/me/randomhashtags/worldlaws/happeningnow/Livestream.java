package me.randomhashtags.worldlaws.happeningnow;

import me.randomhashtags.worldlaws.locale.JSONObjectTranslatable;

public final class Livestream {
    private final String streamerName, title, thumbnailURL;
    private final int viewers;
    private final LivestreamType type;

    public Livestream(String streamerName, String title, String thumbnailURL, int viewers, LivestreamType type) {
        this.streamerName = streamerName;
        this.title = title;
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

    public JSONObjectTranslatable toJSONObject() {
        final JSONObjectTranslatable json = new JSONObjectTranslatable("title");
        json.put("streamerName", streamerName);
        json.put("title", title);
        json.put("thumbnailURL", thumbnailURL);
        json.put("viewers", viewers);
        json.put("type", type.name());
        return json;
    }
}
