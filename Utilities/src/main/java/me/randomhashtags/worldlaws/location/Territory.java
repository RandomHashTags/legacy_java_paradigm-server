package me.randomhashtags.worldlaws.location;

import me.randomhashtags.worldlaws.APIVersion;
import me.randomhashtags.worldlaws.CompletionHandler;
import me.randomhashtags.worldlaws.Jsoupable;
import me.randomhashtags.worldlaws.ServerObject;

public final class Territory implements SovereignState, ServerObject {

    private final String name, flagURL, governmentURL;

    public Territory(String name, String flagURL, String governmentURL) {
        this.name = name;
        this.flagURL = flagURL;
        this.governmentURL = governmentURL;
    }

    @Override
    public String getShortName() {
        return name;
    }
    @Override
    public String getName() {
        return name;
    }
    @Override
    public String getFlagURL() {
        return flagURL;
    }

    @Override
    public void getInformation(APIVersion version, CompletionHandler handler) {
        switch (version) {
            default:
                break;
        }
    }

    public String getGovernmentURL() {
        return governmentURL;
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
