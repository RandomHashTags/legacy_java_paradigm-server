package me.randomhashtags.worldlaws.observances.custom;

import me.randomhashtags.worldlaws.EventDate;
import me.randomhashtags.worldlaws.country.WLCountry;
import me.randomhashtags.worldlaws.observances.IHoliday;

import java.time.DayOfWeek;
import java.time.Month;

public enum FarmersDay implements IHoliday {
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
            case GHANA: return getFirst(DayOfWeek.FRIDAY, Month.DECEMBER, year);
            case INDIA: return new EventDate(Month.DECEMBER, 23, year);
            case PAKISTAN: return new EventDate(Month.DECEMBER, 18, year);
            case SOUTH_KOREA: return new EventDate(Month.NOVEMBER, 11, year);
            case UNITED_STATES: return new EventDate(Month.OCTOBER, 12, year);
            case ZAMBIA: return getFirst(DayOfWeek.MONDAY, Month.AUGUST, year);
            default: return null;
        }
    }
}
