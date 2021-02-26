package me.randomhashtags.worldlaws.country.usa.federal;

import me.randomhashtags.worldlaws.LocalServer;
import me.randomhashtags.worldlaws.country.usa.BillStatus;
import me.randomhashtags.worldlaws.country.usa.USChamber;

import java.time.LocalDate;

public final class PreCongressBill {
    private BillStatus status;
    private final USChamber chamber;
    private final String id, title, committees;
    private final LocalDate date;

    public PreCongressBill(BillStatus status, USChamber chamber, String id, String title, String committees, LocalDate date) {
        this.status = status;
        this.chamber = chamber;
        this.id = id;
        this.title = LocalServer.fixEscapeValues(title);
        this.committees = committees;
        this.date = date;
    }

    public BillStatus getStatus() {
        return status;
    }
    public void setStatus(BillStatus status) {
        this.status = status;
    }
    public USChamber getChamber() {
        return chamber;
    }
    public String getId() {
        return id;
    }
    public String getTitle() {
        return title;
    }
    public String getCommittees() {
        return committees;
    }
    public LocalDate getDate() {
        return date;
    }

    @Override
    public String toString() {
        return "{" +
                "\"status\":\"" + status.name() + "\"," +
                "\"chamber\":\"" + chamber.name() + "\"," +
                "\"id\":\"" + id + "\"," +
                "\"title\":\"" + title + "\"," +
                "\"committees\":\"" + committees + "\"," +
                "\"date\":\"" + date.toString() + "\"" +
                "}";
    }
}
