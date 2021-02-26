package me.randomhashtags.worldlaws.country;

import me.randomhashtags.worldlaws.LocalServer;

public final class Subdivision {

    private String title, description;

    public Subdivision(String title, String description) {
        this.title = LocalServer.fixEscapeValues(title);
        this.description = LocalServer.fixEscapeValues(description);
    }

    public String getTitle() {
        return title;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "{" +
                "\"title\":\"" + title + "\"," +
                "\"description\":\"" + description + "\"" +
                "}";
    }
}
