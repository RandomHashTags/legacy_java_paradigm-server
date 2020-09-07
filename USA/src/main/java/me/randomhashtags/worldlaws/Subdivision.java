package me.randomhashtags.worldlaws;

public final class Subdivision {

    private String title, description;

    public Subdivision(String title, String description) {
        this.title = title;
        this.description = description;
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
        return "{\"title\":\"" + LocalServer.fixEscapeValues(title) + "\"," +
                "\"description\":\"" + LocalServer.fixEscapeValues(description) + "\"" +
                "}";
    }
}
