package me.randomhashtags.worldlaws.location;

import me.randomhashtags.worldlaws.LocalServer;

public final class CountryResource {
    private final String name, url;

    public CountryResource(String name, String url) {
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
