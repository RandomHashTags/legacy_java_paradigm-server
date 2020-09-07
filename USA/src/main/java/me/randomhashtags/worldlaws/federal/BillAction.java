package me.randomhashtags.worldlaws.federal;

import me.randomhashtags.worldlaws.Chamber;
import me.randomhashtags.worldlaws.LocalServer;

import java.time.LocalDateTime;

public final class BillAction {
    private Chamber chamber;
    private LocalDateTime date;
    private String title;

    public BillAction(Chamber chamber, String title, LocalDateTime date) {
        this.chamber = chamber;
        this.title = title;
        this.date = date;
    }

    public Chamber getChamber() {
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
        return "{\"chamber\":\"" + chamber + "\"," +
                "\"title\":\"" + LocalServer.fixEscapeValues(title) + "\"," +
                "\"date\":\"" + date.toString() + "\"" +
                "}";
    }
}
