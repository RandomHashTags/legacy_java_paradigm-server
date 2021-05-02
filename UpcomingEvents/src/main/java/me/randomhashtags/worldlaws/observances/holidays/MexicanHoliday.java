package me.randomhashtags.worldlaws.observances.holidays;

import me.randomhashtags.worldlaws.EventDate;
import me.randomhashtags.worldlaws.location.WLCountry;

import java.time.Month;

public enum MexicanHoliday implements IHoliday {

    CINCO_DE_MAYO,
    CRY_OF_DOLORES,

    DAY_OF_THE_ARMY,

    ;

    private final String wikipediaName;

    MexicanHoliday() {
        this(null);
    }
    MexicanHoliday(String wikipediaName) {
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
            case CINCO_DE_MAYO: return new EventDate(Month.MAY, 5, year);
            case CRY_OF_DOLORES: return new EventDate(Month.SEPTEMBER, 16, year);
            //case DAY_OF_THE_ARMY: return new EventDate(Month.FEBRUARY, 19, year);
            default: return null;
        }
    }
}
