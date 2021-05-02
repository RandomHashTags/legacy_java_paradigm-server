package me.randomhashtags.worldlaws.country;

import me.randomhashtags.worldlaws.LocalServer;
import me.randomhashtags.worldlaws.EventDate;

public final class PreEnactedBill {
    private final Chamber chamber;
    private final String id, title, date;

    public PreEnactedBill(Chamber chamber, String id, String title, EventDate date) {
        this.chamber = chamber;
        this.id = id;
        this.title = LocalServer.fixEscapeValues(title);
        this.date = date.toString();
    }

    @Override
    public String toString() {
        return "{" +
                "\"chamber\":\"" + chamber.getName() + "\"," +
                "\"id\":\"" + id + "\"," +
                "\"title\":\"" + title + "\"," +
                "\"date\":" + date + "" +
                "}";
    }
}
