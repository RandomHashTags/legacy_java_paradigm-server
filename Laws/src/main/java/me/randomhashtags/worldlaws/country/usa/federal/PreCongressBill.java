package me.randomhashtags.worldlaws.country.usa.federal;

import me.randomhashtags.worldlaws.EventDate;
import me.randomhashtags.worldlaws.LocalServer;
import me.randomhashtags.worldlaws.country.usa.USChamber;

public final class PreCongressBill {
    private final USChamber chamber;
    private final String id, title, committees, notes;
    private final EventDate date;

    public PreCongressBill(USChamber chamber, String id, String title, String committees, String notes, EventDate date) {
        this.chamber = chamber;
        this.id = id;
        this.title = LocalServer.fixEscapeValues(title);
        this.committees = committees;
        this.notes = LocalServer.fixEscapeValues(notes);
        this.date = date;
    }

    public EventDate getDate() {
        return date;
    }

    @Override
    public String toString() {
        return "\"" + id + "\":{" +
                "\"chamber\":\"" + chamber.name() + "\"," +
                "\"title\":\"" + title + "\"," +
                "\"committees\":\"" + committees + "\"," +
                (notes != null ? "\"" + notes + "\"," : "") +
                "\"date\":\"" + date.getDateString() + "\"" +
                "}";
    }
}
