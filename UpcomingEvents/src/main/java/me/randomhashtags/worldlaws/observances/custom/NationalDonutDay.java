package me.randomhashtags.worldlaws.observances.custom;

import me.randomhashtags.worldlaws.EventDate;
import me.randomhashtags.worldlaws.country.WLCountry;

import java.time.DayOfWeek;
import java.time.Month;

public enum NationalDonutDay implements CustomHoliday {
    INSTANCE;

    @Override
    public String[] getAliases() {
        return collectAliases("National Doughnut Day");
    }

    public EventDate getDate(WLCountry country, int year) {
        if(country == null) {
            return null;
        }
        switch (country) {
            case AUSTRALIA: return new EventDate(Month.OCTOBER, 25, year);
            case UNITED_STATES: return EventDate.getFirst(DayOfWeek.FRIDAY, Month.JUNE, year);
            default: return null;
        }
    }
}
