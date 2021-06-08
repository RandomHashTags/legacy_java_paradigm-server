package me.randomhashtags.worldlaws.location;

import me.randomhashtags.worldlaws.CompletionHandler;
import me.randomhashtags.worldlaws.Jsoupable;
import me.randomhashtags.worldlaws.ServerObject;

public final class Territory implements Jsoupable, ServerObject {

    private final String name, flagURL, governmentURL;

    public Territory(String name, String flagURL, String governmentURL) {
        this.name = name;
        this.flagURL = flagURL;
        this.governmentURL = governmentURL;
    }

    public String getName() {
        return name;
    }
    public String getFlagURL() {
        return flagURL;
    }
    public String getGovernmentURL() {
        return governmentURL;
    }

    public void getInformation(CompletionHandler handler) {
    }

    @Override
    public String toString() {
        return "{" +
                (governmentURL != null ? "\"governmentURL\":\"" + governmentURL + "\"" : "") +
                "}";
    }

    @Override
    public String toServerJSON() {
        return "\"" + getName() + "\":{" +
                (flagURL != null ? "\"flagURL\":\"" + flagURL + "\"" : "") +
                "}";
    }
}
