package me.randomhashtags.worldlaws.observances.custom;

import me.randomhashtags.worldlaws.EventDate;
import me.randomhashtags.worldlaws.country.WLCountry;
import me.randomhashtags.worldlaws.observances.IHoliday;

import java.time.Month;

public enum VictoryDay implements IHoliday { // https://en.wikipedia.org/wiki/Victory_Day / https://en.wikipedia.org/wiki/Liberation_Day
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
        return null;
    }

    public EventDate getDate(WLCountry country, int year) {
        switch (country) {
            case AFGHANISTAN: return new EventDate(Month.APRIL, 28, year);
            case AZERBAIJAN: return new EventDate(Month.NOVEMBER, 8, year);
            case BANGLADESH: return new EventDate(Month.DECEMBER, 16, year);
            case BELARUS:
            case KAZAKHSTAN:
            case MOLDOVA:
            case RUSSIA:
            case SRI_LANKA:
            case TRANSNISTRIA:
            case UKRAINE:
                return new EventDate(Month.MAY, 9, year);
            case CAMBODIA: return new EventDate(Month.JANUARY, 7, year);
            case ESTONIA: return new EventDate(Month.JUNE, 23, year);
            case POLAND: return new EventDate(Month.MAY, 8, year);
            case TURKEY: return new EventDate(Month.AUGUST, 30, year);
            case VIETNAM: return new EventDate(Month.APRIL, 30, year);
        }
        return null;
    }
}
