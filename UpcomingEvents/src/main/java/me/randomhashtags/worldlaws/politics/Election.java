package me.randomhashtags.worldlaws.politics;

import me.randomhashtags.worldlaws.LocalServer;

public final class Election {
    private final String id, name;

    public Election(String id, String name) {
        this.id = id;
        this.name = LocalServer.fixEscapeValues(name);
    }

    @Override
    public String toString() {
        return "\"" + id + "\":{" +
                "\"name\":\"" + name + "\"" +
                "}";
    }
}
