package me.randomhashtags.worldlaws.country;

import me.randomhashtags.worldlaws.LocalServer;

public final class PreEnactedBill {
    private final String id, title;

    public PreEnactedBill(String id, String title) {
        this.id = id;
        this.title = LocalServer.fixEscapeValues(title);
    }

    @Override
    public String toString() {
        return "\"" + id + "\":{" +
                "\"title\":\"" + title + "\"" +
                "}";
    }
}
