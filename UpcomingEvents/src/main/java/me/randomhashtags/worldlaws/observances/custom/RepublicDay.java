package me.randomhashtags.worldlaws.observances.custom;

import me.randomhashtags.worldlaws.EventDate;
import me.randomhashtags.worldlaws.country.WLCountry;

import java.time.Month;

public enum RepublicDay implements CustomHoliday {
    INSTANCE;

    @Override
    public String getWikipediaName() {
        return "Republic Day";
    }

    @Override
    public EventDate getDate(WLCountry country, int year) {
        switch (country) {
            case SWITZERLAND: return new EventDate(Month.MARCH, 1, year);
        }
        return null;
    }
}
