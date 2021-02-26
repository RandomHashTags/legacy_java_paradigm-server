package me.randomhashtags.worldlaws.location;

import me.randomhashtags.worldlaws.Jsoupable;

public interface Territory extends Jsoupable {
    String getName();
    String getFlagURL();
    String getGovernmentURL();

    default String toJSON() {
        final String flagURL = getFlagURL(), governmentURL = getGovernmentURL();
        return "{" +
                (governmentURL != null ? "\"governmentURL\":\"" + governmentURL + "\"," : "") +
                (flagURL != null ? "\"flagURL\":\"" + flagURL + "\"," : "") +
                "\"name\":\"" + getName() + "\"" +
                "}";
    }
}
