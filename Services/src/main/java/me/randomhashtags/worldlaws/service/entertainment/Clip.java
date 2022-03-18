package me.randomhashtags.worldlaws.service.entertainment;

import me.randomhashtags.worldlaws.EventSources;
import me.randomhashtags.worldlaws.LocalServer;
import me.randomhashtags.worldlaws.locale.JSONObjectTranslatable;

public final class Clip {
    private final String title, game, thumbnail, embedHTML;
    private final ClipBroadcaster broadcaster;
    private final long viewCount;
    private final float duration;
    private final EventSources sources;

    public Clip(String title, ClipBroadcaster broadcaster, String game, String thumbnail, long viewCount, float duration, String embedHTML, EventSources sources) {
        this.title = LocalServer.fixEscapeValues(title);
        this.broadcaster = broadcaster;
        this.game = LocalServer.fixEscapeValues(game);
        this.thumbnail = thumbnail;
        this.viewCount = viewCount;
        this.duration = duration;
        this.embedHTML = embedHTML;
        this.sources = sources;
    }

    @Override
    public String toString() {
        return toJSONObject().toString();
    }
    public JSONObjectTranslatable toJSONObject() {
        final JSONObjectTranslatable json = new JSONObjectTranslatable();
        json.put("embedHTML", embedHTML);
        json.put("sources", sources.toJSONObject());
        return json;
    }
    public JSONObjectTranslatable toPreJSONObject() {
        final JSONObjectTranslatable json = new JSONObjectTranslatable();
        json.put("title", title);
        json.put("broadcaster", broadcaster);
        json.put("game", game);
        json.put("thumbnail", thumbnail);
        json.put("viewCount", viewCount);
        json.put("duration", duration);
        return json;
    }
}
