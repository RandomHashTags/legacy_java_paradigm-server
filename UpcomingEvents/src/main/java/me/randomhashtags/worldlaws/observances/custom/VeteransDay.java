package me.randomhashtags.worldlaws.observances.custom;

import me.randomhashtags.worldlaws.EventDate;
import me.randomhashtags.worldlaws.country.WLCountry;
import me.randomhashtags.worldlaws.observances.IHoliday;

import java.time.DayOfWeek;
import java.time.Month;

public enum VeteransDay implements IHoliday {
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
            case FINLAND: return new EventDate(Month.APRIL, 27, year);
            case NETHERLANDS:
            case UNITED_KINGDOM:
                return getLast(DayOfWeek.SATURDAY, Month.JUNE, year);
            case NORWAY: return new EventDate(Month.MAY, 8, year);
            case SOUTH_KOREA: return new EventDate(Month.OCTOBER, 8, year);
            case SWEDEN: return new EventDate(Month.MAY, 29, year);
            case UNITED_STATES: return new EventDate(Month.NOVEMBER, 11, year);
            default: return null;
        }
    }
}
