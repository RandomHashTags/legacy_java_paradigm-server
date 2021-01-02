package me.randomhashtags.worldlaws.elections;

import me.randomhashtags.worldlaws.LocalServer;
import me.randomhashtags.worldlaws.event.EventDate;

public final class Election {
    private long id;
    private String name, ocdDivisionId;
    private EventDate date;

    public Election(long id, String name, EventDate date, String ocdDivisionId) {
        this.id = id;
        this.name = LocalServer.fixEscapeValues(name);
        this.date = date;
        this.ocdDivisionId = ocdDivisionId;
    }

    public long getID() {
        return id;
    }
    public String getName() {
        return name;
    }
    public EventDate getElectionDay() {
        return date;
    }
    public String getOCDDivisionID() {
        return ocdDivisionId;
    }

    @Override
    public String toString() {
        return "{" +
                "\"id\":" + id + "," +
                "\"name\":\"" + name + "\"," +
                "\"date\":" + date.toString() + "," +
                "\"ocdDivisionId\":\"" + ocdDivisionId + "\"" +
                "}";
    }
}
