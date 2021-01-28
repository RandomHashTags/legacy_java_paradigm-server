package me.randomhashtags.worldlaws.event;

import me.randomhashtags.worldlaws.NotNull;

import java.text.SimpleDateFormat;
import java.time.Month;
import java.util.Date;

public final class EventDate {
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss");
    private final Month month;
    private final int day, year;
    // if year == -1 : no year specified
    // if year == -2 : annually

    public EventDate(@NotNull Month month, int day, int year) {
        this.month = month;
        this.day = day;
        this.year = year;
    }

    public EventDate(long epoch) {
        if(epoch > 0) {
            final String dateString = DATE_FORMAT.format(new Date(epoch));
            final String[] values = dateString.split(" "), dates = values[0].split("-"), times = values[1].split(":");
            this.month = Month.of(Integer.parseInt(dates[0]));
            this.day = Integer.parseInt(dates[1]);
            this.year = Integer.parseInt(dates[2]);
        } else {
            month = Month.JANUARY;
            day = -1;
            year = -1;
        }
    }

    public EventDate(String input) {
        final String[] values = input.split("T");
        final String[] dates = values[0].split("-"), times = values[1].split("Z")[0].split(":");
        month = Month.of(Integer.parseInt(dates[1]));
        day = Integer.parseInt(dates[2]);
        year = Integer.parseInt(dates[0]);
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
