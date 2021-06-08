package me.randomhashtags.worldlaws.observances.type;

import me.randomhashtags.worldlaws.EventDate;
import me.randomhashtags.worldlaws.location.WLCountry;
import me.randomhashtags.worldlaws.observances.IHoliday;

import java.time.DayOfWeek;
import java.time.Month;

public enum AustralianHoliday implements IHoliday {

    AUSTRALIA_DAY,
    WESTERN_AUSTRALIA_DAY,

    ;

    private final String wikipediaName;

    AustralianHoliday() {
        this(null);
    }
    AustralianHoliday(String wikipediaName) {
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
        switch (this) {
            case AUSTRALIA_DAY: return new String[] { "ANA Day", "Anniversary Day", "Foundation Day", "Invasion Day", "Survival Day", "Day of Mourning" };
            case WESTERN_AUSTRALIA_DAY: return new String[] { "Foundation Day", "WA Day" };
            default: return null;
        }
    }

    @Override
    public EventDate getDate(WLCountry country, int year) {
        switch (this) {
            case AUSTRALIA_DAY: return new EventDate(Month.JANUARY, 26, year);
            case WESTERN_AUSTRALIA_DAY: return getFirst(DayOfWeek.MONDAY, Month.JUNE, year);
            default: return null;
        }
    }
}
