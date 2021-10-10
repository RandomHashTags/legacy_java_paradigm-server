package me.randomhashtags.worldlaws.observances.custom;

import me.randomhashtags.worldlaws.EventDate;
import me.randomhashtags.worldlaws.country.WLCountry;

import java.time.Month;

public enum NationalScienceDay implements CustomIHoliday {
    INSTANCE;

    @Override
    public String getOfficialName() {
        return "National Science Day";
    }

    @Override
    public EventDate getDate(WLCountry country, int year) {
        switch (country) {
            case INDIA: return new EventDate(Month.FEBRUARY, 28, year);
            default: return null;
        }
    }
}
