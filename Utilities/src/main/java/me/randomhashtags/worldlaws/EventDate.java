package me.randomhashtags.worldlaws;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Month;
import java.util.Date;

public final class EventDate {
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss");
    private final Month month;
    private final int day, year, hour, minute;
    // if year == -1 : no year specified
    // if year == -2 : annually

    public EventDate(@NotNull Month month, int day, int year) {
        this.month = month;
        this.day = day;
        this.year = year;
        hour = -1;
        minute = -1;
    }
    public EventDate(long epoch) {
        if(epoch > 0) {
            final String dateString = DATE_FORMAT.format(new Date(epoch));
            final String[] values = dateString.split(" "), dates = values[0].split("-"), times = values[1].split(":");
            this.month = Month.of(Integer.parseInt(dates[0]));
            this.day = Integer.parseInt(dates[1]);
            this.year = Integer.parseInt(dates[2]);
            this.hour = Integer.parseInt(times[0]);
            this.minute = Integer.parseInt(times[1]);
        } else {
            month = Month.JANUARY;
            day = -1;
            year = -1;
            hour = -1;
            minute = -1;
        }
    }
    public EventDate(String input) {
        final String[] values = input.split("T");
        final String[] dates = values[0].split("-"), times = values[1].split("Z")[0].split(":");
        month = Month.of(Integer.parseInt(dates[1]));
        day = Integer.parseInt(dates[2]);
        year = Integer.parseInt(dates[0]);
        final int hour = Integer.parseInt(times[0]), minute = Integer.parseInt(times[1]);
        this.hour = hour > 0 ? hour : -1;
        this.minute = minute > 0 ? minute : -1;
    }
    public EventDate(JSONObject json) {
        month = Month.valueOf(json.getString("month").toUpperCase());
        day = json.has("day") ? json.getInt("day") : -1;
        year = json.has("year") ? json.getInt("year") : -1;
        hour = json.has("hour") ? json.getInt("hour") : -1;
        minute = json.has("minute") ? json.getInt("minute") : -1;
    }
    public EventDate(LocalDate date) {
        month = date.getMonth();
        day = date.getDayOfMonth();
        year = date.getYear();
        hour = -1;
        minute = -1;
    }

    public String getDateString() {
        return month.getValue() + "-" + year + "-" + day;
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

    public LocalDate getLocalDate() {
        return LocalDate.of(year, month, day);
    }
    public EventDate plusDays(long days) {
        final LocalDate date = getLocalDate().plusDays(days);
        return new EventDate(date);
    }
    public EventDate minusDays(long days) {
        final LocalDate date = getLocalDate().minusDays(days);
        return new EventDate(date);
    }
    public EventDate getFirstWeekdayAfter() {
        final LocalDate date = getLocalDate();
        switch (date.getDayOfWeek()) {
            case FRIDAY: return new EventDate(date.plusDays(3));
            case SATURDAY: return new EventDate(date.plusDays(2));
            default: return new EventDate(date.plusDays(1));
        }
    }

    @Override
    public String toString() {
        return "{" +
                "\"month\":\"" + month.name() + "\"," +
                "\"day\":" + day + "," +
                (hour > -1 ? "\"hour\":" + hour + "," : "") +
                (minute > -1 ? "\"minute\":" + minute + "," : "") +
                "\"year\":" + year +
                "}";
    }
}
