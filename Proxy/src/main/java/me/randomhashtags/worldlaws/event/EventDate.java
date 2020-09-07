package me.randomhashtags.worldlaws.event;

import me.randomhashtags.worldlaws.NotNull;

import java.time.Month;

public final class EventDate {
    private Month month;
    private int day, year;
    // if year == -1 : no year specified
    // if year == -2 : annually

    public EventDate(@NotNull Month month, int day, int year) {
        this.month = month;
        this.day = day;
        this.year = year;
    }

    public Month getMonth() {
        return month;
    }
    public int getDay() {
        return day;
    }
    public int getYear() {
        return year;
    }

    @Override
    public String toString() {
        return "{" +
                "\"month\":\"" + month.name() + "\"," +
                "\"day\":" + day + "," +
                "\"year\":" + year +
                "}";
    }
}
