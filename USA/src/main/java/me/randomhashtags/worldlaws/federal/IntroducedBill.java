package me.randomhashtags.worldlaws.federal;

import me.randomhashtags.worldlaws.Chamber;
import me.randomhashtags.worldlaws.LocalServer;

import java.time.LocalDate;

public final class IntroducedBill {
    private Chamber chamber;
    private String id, title, committees;
    private LocalDate date;

    public IntroducedBill(Chamber chamber, String id, String title, String committees, LocalDate date) {
        this.chamber = chamber;
        this.id = id;
        this.title = title;
        this.committees = committees;
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
    public String getCommittees() {
        return committees;
    }
    public LocalDate getDate() {
        return date;
    }

    @Override
    public String toString() {
        return "{\"chamber\":\"" + chamber.name() + "\"," +
                "\"id\":\"" + id + "\"," +
                "\"title\":\"" + LocalServer.fixEscapeValues(title) + "\"," +
                "\"committees\":\"" + committees + "\"," +
                "\"date\":\"" + date.toString() + "\"" +
                "}";
    }
}
