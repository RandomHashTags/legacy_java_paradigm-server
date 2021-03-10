package me.randomhashtags.worldlaws.country;

import me.randomhashtags.worldlaws.LocalServer;

import java.time.LocalDate;

public final class PreEnactedBill {
    private final Chamber chamber;
    private final String id, title;
    private final LocalDate date;

    public PreEnactedBill(Chamber chamber, String id, String title, LocalDate date) {
        this.chamber = chamber;
        this.id = id;
        this.title = LocalServer.fixEscapeValues(title);
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
        return "{" +
                "\"chamber\":\"" + chamber.getName() + "\"," +
                "\"id\":\"" + id + "\"," +
                "\"title\":\"" + title + "\"," +
                "\"date\":\"" + date.toString() + "\"" +
                "}";
    }
}
