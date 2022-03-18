package me.randomhashtags.worldlaws.observances.type;

import me.randomhashtags.worldlaws.EventDate;
import me.randomhashtags.worldlaws.country.WLCountry;
import me.randomhashtags.worldlaws.observances.Holiday;
import me.randomhashtags.worldlaws.observances.HolidayType;

import java.time.Month;

public enum GregorianHoliday implements Holiday {

    NEW_YEARS_DAY(
            "New Year's Day"
    ),
    NEW_YEARS_EVE(
            "New Year's Eve"
    ),

    ;

    private final String wikipediaName;

    GregorianHoliday() {
        this(null);
    }
    GregorianHoliday(String wikipediaName) {
        this.wikipediaName = wikipediaName;
    }

    @Override
    public HolidayType getType() {
        return HolidayType.GREGORIAN;
    }

    @Override
    public String getWikipediaName() {
        return wikipediaName;
    }

    @Override
    public String[] getAliases() {
        return null;
    }

    @Override
    public EventDate getDate(WLCountry country, int year) {
        switch (this) {
            case NEW_YEARS_DAY: return new EventDate(Month.JANUARY, 1, year);
            case NEW_YEARS_EVE: return new EventDate(Month.DECEMBER, 31, year);
        }
        return null;
    }
}
