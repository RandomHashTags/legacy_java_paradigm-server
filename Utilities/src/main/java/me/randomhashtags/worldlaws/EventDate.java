package me.randomhashtags.worldlaws;

import me.randomhashtags.worldlaws.locale.JSONObjectTranslatable;
import org.json.JSONObject;

import java.time.*;

public final class EventDate {
    private static final ZoneId UTC_OFFSET = ZoneId.ofOffset("", ZoneOffset.UTC);

    public static EventDate getFirst(DayOfWeek dayOfWeek, Month month, int year) {
        return get(1, dayOfWeek, year, month, 1);
    }
    public static EventDate getSecond(DayOfWeek dayOfWeek, Month month, int year) {
        return get(2, dayOfWeek, year, month, 1);
    }
    public static EventDate getThird(DayOfWeek dayOfWeek, Month month, int year) {
        return get(3, dayOfWeek, year, month, 1);
    }
    public static EventDate getFourth(DayOfWeek dayOfWeek, Month month, int year) {
        return get(4, dayOfWeek, year, month, 1);
    }
    public static EventDate getLast(DayOfWeek dayOfWeek, Month month, int year) {
        final EventDate fifth = get(5, dayOfWeek, year, month, 1);
        return fifth != null ? fifth : getFourth(dayOfWeek, month, year);
    }
    public static EventDate getFirstAfter(DayOfWeek dayOfWeek, int year, Month month, int day) {
        return get(1, dayOfWeek, year, month, day);
    }
    private static EventDate get(int amount, DayOfWeek dayOfWeek, int year, Month month, int day) {
        final LocalDate date = LocalDate.of(year, month, day);
        final int startingDay = amount == 1 ? 1 : (amount-1)*7;
        for(int i = startingDay; i <= startingDay+7; i++) {
            final LocalDate targetDate = date.plusDays(i);
            final Month targetMonth = targetDate.getMonth();
            final DayOfWeek weekday = targetDate.getDayOfWeek();
            if(dayOfWeek == weekday && targetMonth == month) {
                return new EventDate(month, targetDate.getDayOfMonth(), year);
            }
        }
        return null;
    }

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
            final LocalDateTime date = Instant.ofEpochMilli(epoch).atZone(UTC_OFFSET).toLocalDateTime();
            this.month = date.getMonth();
            this.day = date.getDayOfMonth();
            this.year = date.getYear();
            this.hour = date.getHour();
            this.minute = date.getMinute();
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

    public boolean equals(EventDate right) {
        return month == right.month && year == right.year && day == right.day && hour == right.hour && minute == right.minute;
    }
    public String getDateString() {
        return getDateString(year, day, month);
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
    public long toEpoch() {
        return getLocalDate().toEpochDay();
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

    public static EventDate valueOfDateString(String dateString) {
        final String[] values = dateString.split("-");
        final int monthValue = Integer.parseInt(values[0]), year = Integer.parseInt(values[1]), day = Integer.parseInt(values[2]);
        final Month month = Month.of(monthValue);
        return new EventDate(month, day, year);
    }
    public static String getDateString(LocalDate date) {
        return getDateString(date.getYear(), date.getDayOfMonth(), date.getMonth());
    }
    public static String getDateString(int year, int day, Month month) {
        return month.getValue() + "-" + year + "-" + day;
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

    public JSONObjectTranslatable toJSONObject() {
        final JSONObjectTranslatable json = new JSONObjectTranslatable();
        json.put("month", month.name());
        json.put("day", day);
        json.put("year", year);
        if(hour > -1) {
            json.put("hour", hour);
        }
        if(minute > -1) {
            json.put("minute", minute);
        }
        return json;
    }
}
