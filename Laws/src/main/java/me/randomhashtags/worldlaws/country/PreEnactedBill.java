package me.randomhashtags.worldlaws.country;

import me.randomhashtags.worldlaws.LocalServer;
import me.randomhashtags.worldlaws.locale.JSONObjectTranslatable;

public final class PreEnactedBill {
    private final String id, title;

    public PreEnactedBill(String id, String title) {
        this.id = id;
        this.title = LocalServer.fixEscapeValues(title);
    }

    public String getID() {
        return id;
    }

    public JSONObjectTranslatable toJSONObject() {
        final JSONObjectTranslatable json = new JSONObjectTranslatable("title");
        json.put("title", title);
        return json;
    }
}
