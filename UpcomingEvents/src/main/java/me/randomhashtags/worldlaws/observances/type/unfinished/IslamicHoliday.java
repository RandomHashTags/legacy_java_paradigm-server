package me.randomhashtags.worldlaws.observances.type.unfinished;

import me.randomhashtags.worldlaws.EventDate;
import me.randomhashtags.worldlaws.country.WLCountry;
import me.randomhashtags.worldlaws.observances.Holiday;
import me.randomhashtags.worldlaws.observances.HolidayType;

public enum IslamicHoliday implements Holiday {

    DAY_OF_ARAFAH,

    HAJJ,

    START_OF_RAMADAN(
            "Ramadan"
    ),
    END_OF_RAMADAN(
            "Ramadan"
    ),

    ;

    private final String wikipediaName;

    IslamicHoliday() {
        this(null);
    }
    IslamicHoliday(String wikipediaName) {
        this.wikipediaName = wikipediaName;
    }

    @Override
    public HolidayType getType() {
        return null;
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
        }
        return null;
    }
}
