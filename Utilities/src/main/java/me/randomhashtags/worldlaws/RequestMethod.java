package me.randomhashtags.worldlaws;

public enum RequestMethod {
    GET,
    POST;

    private String name;

    RequestMethod() {
        name = name();
    }

    public String getName() {
        return name;
    }
}
