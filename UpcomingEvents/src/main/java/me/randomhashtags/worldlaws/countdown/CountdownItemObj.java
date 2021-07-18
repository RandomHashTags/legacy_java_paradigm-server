package me.randomhashtags.worldlaws.countdown;

import me.randomhashtags.worldlaws.EventDate;

public final class CountdownItemObj implements CountdownItem {
    private final String title;
    private final EventDate date, endDate;

    public CountdownItemObj(String title, EventDate date) {
        this(title, date, null);
    }
    public CountdownItemObj(String title, EventDate date, EventDate endDate) {
        this.title = title;
        this.date = date;
        this.endDate = endDate;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public EventDate getDate() {
        return date;
    }

    @Override
    public EventDate getEndDate() {
        return endDate;
    }

    @Override
    public String toString() {
        return "\"" + title + "\":{" +
                (endDate != null ? "\"endDate\":" + endDate.toString() + "," : "") +
                "\"date\":" + date.toString() +
                "}";
    }
}
