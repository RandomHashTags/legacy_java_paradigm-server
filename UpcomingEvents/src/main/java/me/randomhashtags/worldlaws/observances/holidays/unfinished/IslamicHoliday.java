package me.randomhashtags.worldlaws.observances.holidays.unfinished;

import me.randomhashtags.worldlaws.EventDate;
import me.randomhashtags.worldlaws.location.WLCountry;
import me.randomhashtags.worldlaws.observances.holidays.IHoliday;

public enum IslamicHoliday implements IHoliday {

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
    public Enum<? extends IHoliday> getEnum() {
        return this;
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
