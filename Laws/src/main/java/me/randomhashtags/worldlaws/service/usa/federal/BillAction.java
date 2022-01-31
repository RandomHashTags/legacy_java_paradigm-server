package me.randomhashtags.worldlaws.service.usa.federal;

import me.randomhashtags.worldlaws.LocalServer;
import me.randomhashtags.worldlaws.service.usa.USChamber;

import java.time.LocalDateTime;

public final class BillAction {
    private final USChamber chamber;
    private final LocalDateTime date;
    private final String title;

    public BillAction(USChamber chamber, String title, LocalDateTime date) {
        this.chamber = chamber;
        this.title = LocalServer.fixEscapeValues(title);
        this.date = date;
    }

    public USChamber getChamber() {
        return chamber;
    }
    public LocalDateTime getDate() {
        return date;
    }
    public String getTitle() {
        return title;
    }

    @Override
    public String toString() {
        return "{" +
                (chamber != null ? "\"chamber\":\"" + chamber + "\"," : "") +
                "\"title\":\"" + title + "\"," +
                "\"date\":\"" + date.toString() + "\"" +
                "}";
    }
}
