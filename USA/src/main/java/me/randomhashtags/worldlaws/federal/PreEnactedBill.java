package me.randomhashtags.worldlaws.federal;

import me.randomhashtags.worldlaws.Chamber;
import me.randomhashtags.worldlaws.LocalServer;

import java.time.LocalDate;

public final class PreEnactedBill {
    private Chamber chamber;
    private String id, title;
    private LocalDate date;

    public PreEnactedBill(Chamber chamber, String id, String title, LocalDate date) {
        this.chamber = chamber;
        this.id = id;
        this.title = title;
        this.date = date;
    }

    public Chamber getChamber() {
        return chamber;
    }
    public String getId() {
        return id;
    }
    public String getTitle() {
        return title;
    }
    public LocalDate getDate() {
        return date;
    }

    @Override
    public String toString() {
        return "{\"chamber\":\"" + chamber.name() + "\"," +
                "\"id\":\"" + id + "\"," +
                "\"title\":\"" + LocalServer.fixEscapeValues(title) + "\"," +
                "\"date\":\"" + date.toString() + "\"" +
                "}";
    }
}
