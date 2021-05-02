package me.randomhashtags.worldlaws.hurricanes;

import me.randomhashtags.worldlaws.LocalServer;
import me.randomhashtags.worldlaws.EventDate;

public final class Hurricane {
    private String name, description, peak;
    private EventDate startDate, endDate;

    public Hurricane(String name, String description, String peak, EventDate startDate, EventDate endDate) {
        this.name = name;
        this.description = LocalServer.fixEscapeValues(description);
        this.peak = peak;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public String getName() {
        return name;
    }
    public String getDescription() {
        return description;
    }
    public String getPeak() {
        return peak;
    }
    public EventDate getStartDate() {
        return startDate;
    }
    public EventDate getEndDate() {
        return endDate;
    }

    @Override
    public String toString() {
        return "{" +
                "\"name\":\"" + name + "\"," +
                "\"description\":\"" + description + "\"," +
                "\"peak\":\"" + peak + "\"," +
                "\"startDate\":" + (startDate != null ? startDate.toString() : "null") + "," +
                "\"endDate\":" + (endDate != null ? endDate.toString() : "null") +
                "}";
    }
}
