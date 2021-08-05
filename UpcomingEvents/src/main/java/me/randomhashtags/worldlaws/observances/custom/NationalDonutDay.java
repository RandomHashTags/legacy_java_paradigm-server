package me.randomhashtags.worldlaws.observances.custom;

import me.randomhashtags.worldlaws.EventDate;
import me.randomhashtags.worldlaws.country.WLCountry;
import me.randomhashtags.worldlaws.observances.IHoliday;

import java.time.DayOfWeek;
import java.time.Month;

public enum NationalDonutDay implements IHoliday {
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
        return new String[] {
                "National Doughnut Day"
        };
    }

    public EventDate getDate(WLCountry country, int year) {
        switch (country) {
            case AUSTRALIA: return new EventDate(Month.OCTOBER, 25, year);
            case UNITED_STATES: return getFirst(DayOfWeek.FRIDAY, Month.JUNE, year);
            default: return null;
        }
    }
}
