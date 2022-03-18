package me.randomhashtags.worldlaws.observances.custom;

import me.randomhashtags.worldlaws.EventDate;
import me.randomhashtags.worldlaws.country.WLCountry;

import java.time.Month;

public enum InternationalWorkersDay implements CustomHoliday {
    INSTANCE;

    @Override
    public String getWikipediaName() {
        return "International Workers' Day";
    }

    @Override
    public String[] getAliases() {
        return collectAliases("Labour Day", "May Day");
    }

    @Override
    public EventDate getDate(WLCountry country, int year) {
        switch (country) {
            case GERMANY:
            case SWEDEN:
                return new EventDate(Month.MAY, 1, year);
            default:
                return null;
        }
    }
}
