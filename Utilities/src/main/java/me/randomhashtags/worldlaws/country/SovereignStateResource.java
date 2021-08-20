package me.randomhashtags.worldlaws.country;

import me.randomhashtags.worldlaws.LocalServer;

public final class SovereignStateResource {
    private final String name, url;

    public SovereignStateResource(String name, String url) {
        this.name = LocalServer.fixEscapeValues(name);
        this.url = url;
    }

    @Override
    public String toString() {
        return "\"" + name + "\":{" +
                "\"url\":\"" + url + "\"" +
                "}";
    }
}
