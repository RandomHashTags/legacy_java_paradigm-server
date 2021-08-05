package me.randomhashtags.worldlaws.observances.custom;

import me.randomhashtags.worldlaws.EventDate;
import me.randomhashtags.worldlaws.country.WLCountry;
import me.randomhashtags.worldlaws.observances.IHoliday;

import java.time.DayOfWeek;
import java.time.Month;

public enum Thanksgiving implements IHoliday {
    INSTANCE;

    @Override
    public Enum<? extends IHoliday> getEnum() {
        return null;
    }

    @Override
    public String getOfficialName() {
        return null;
    }

    @Override
    public String[] getAliases() {
        return null;
    }

    public EventDate getDate(WLCountry country, int year) {
        switch (country) {
            case CANADA: return getSecond(DayOfWeek.MONDAY, Month.OCTOBER, year);
            case BRAZIL:
            case PHILIPPINES:
            case NETHERLANDS:
            case UNITED_STATES:
                return getLast(DayOfWeek.THURSDAY, Month.NOVEMBER, year);
            case GERMANY: return getFirst(DayOfWeek.SUNDAY, Month.OCTOBER, year);
            case JAPAN: return new EventDate(Month.NOVEMBER, 23, year);
            case LIBERIA: return getFirst(DayOfWeek.THURSDAY, Month.NOVEMBER, year);
            case RWANDA: return getFirst(DayOfWeek.FRIDAY, Month.AUGUST, year);
            default: return null;
        }
    }
}
