package me.randomhashtags.worldlaws.observances.custom;

import me.randomhashtags.worldlaws.EventDate;
import me.randomhashtags.worldlaws.country.WLCountry;

import java.time.DayOfWeek;
import java.time.Month;

public enum Thanksgiving implements CustomHoliday {
    INSTANCE;

    @Override
    public String getWikipediaName() {
        return "Thanksgiving";
    }

    @Override
    public EventDate getDate(WLCountry country, int year) {
        switch (country) {
            case CANADA: return EventDate.getSecond(DayOfWeek.MONDAY, Month.OCTOBER, year);
            case BRAZIL:
            case PHILIPPINES:
            case NETHERLANDS:
            case UNITED_STATES:
                return EventDate.getLast(DayOfWeek.THURSDAY, Month.NOVEMBER, year);
            case GERMANY: return EventDate.getFirst(DayOfWeek.SUNDAY, Month.OCTOBER, year);
            case JAPAN: return new EventDate(Month.NOVEMBER, 23, year);
            case LIBERIA: return EventDate.getFirst(DayOfWeek.THURSDAY, Month.NOVEMBER, year);
            case RWANDA: return EventDate.getFirst(DayOfWeek.FRIDAY, Month.AUGUST, year);
            default: return null;
        }
    }
}
