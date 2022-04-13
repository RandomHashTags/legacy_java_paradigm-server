package me.randomhashtags.worldlaws.politics;

import me.randomhashtags.worldlaws.locale.JSONObjectTranslatable;

public final class Election {
    private final String id, name;

    public Election(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getID() {
        return id;
    }

    public JSONObjectTranslatable toJSONObject() {
        final JSONObjectTranslatable json = new JSONObjectTranslatable("name");
        json.put("name", name);
        return json;
    }
}
