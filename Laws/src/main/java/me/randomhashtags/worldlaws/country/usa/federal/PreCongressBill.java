package me.randomhashtags.worldlaws.country.usa.federal;

import me.randomhashtags.worldlaws.LocalServer;
import me.randomhashtags.worldlaws.country.usa.BillStatus;
import me.randomhashtags.worldlaws.country.usa.USChamber;
import me.randomhashtags.worldlaws.EventDate;

public final class PreCongressBill {
    private BillStatus status;
    private final USChamber chamber;
    private final String id, title, committees, notes;
    private final EventDate date;

    public PreCongressBill(BillStatus status, USChamber chamber, String id, String title, String committees, String notes, EventDate date) {
        this.status = status;
        this.chamber = chamber;
        this.id = id;
        this.title = LocalServer.fixEscapeValues(title);
        this.committees = committees;
        this.notes = LocalServer.fixEscapeValues(notes);
        this.date = date;
    }

    public void setStatus(BillStatus status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "{" +
                "\"status\":\"" + status.name() + "\"," +
                "\"chamber\":\"" + chamber.name() + "\"," +
                "\"id\":\"" + id + "\"," +
                "\"title\":\"" + title + "\"," +
                "\"committees\":\"" + committees + "\"," +
                (notes != null ? "\"" + notes + "\"," : "") +
                "\"date\":\"" + date.toString() + "\"" +
                "}";
    }
}
