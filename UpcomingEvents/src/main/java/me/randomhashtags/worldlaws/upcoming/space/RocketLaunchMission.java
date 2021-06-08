package me.randomhashtags.worldlaws.upcoming.space;

import me.randomhashtags.worldlaws.LocalServer;

public final class RocketLaunchMission {
    private final String name, description, type;

    public RocketLaunchMission(String name, String description, String type) {
        this.name = LocalServer.fixEscapeValues(name);
        this.description = LocalServer.fixEscapeValues(description);
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return "{" +
                "\"name\":\"" + name + "\"," +
                "\"description\":\"" + description + "\"," +
                "\"type\":\"" + type + "\"" +
                "}";
    }
}
