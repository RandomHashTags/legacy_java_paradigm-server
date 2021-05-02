package me.randomhashtags.worldlaws.observances.holidays.custom;

import me.randomhashtags.worldlaws.EventDate;
import me.randomhashtags.worldlaws.location.WLCountry;
import me.randomhashtags.worldlaws.observances.holidays.IHoliday;

import java.time.DayOfWeek;
import java.time.Month;

public enum HeroesDay implements IHoliday { // https://en.wikipedia.org/wiki/Heroes%27_Day
    INSTANCE;

    @Override
    public Enum<? extends IHoliday> getEnum() {
        return null;
    }

    @Override
    public String getWikipediaName() {
        return null;
    }

    @Override
    public String[] getAliases() {
        return null;
    }

    public EventDate getDate(WLCountry country, int year) {
        switch (country) {
            case ANGOLA: return new EventDate(Month.SEPTEMBER, 17, year);
            case BARBADOS: return new EventDate(Month.APRIL, 28, year);
            case BERMUDA: return getThird(DayOfWeek.MONDAY, Month.JUNE, year);
            case CAPE_VERDE: return new EventDate(Month.JANUARY, 20, year);
            case EAST_TIMOR: return new EventDate(Month.DECEMBER, 31, year);
            case HUNGARY: return new EventDate(Month.MAY, 31, year);
            case INDONESIA: return new EventDate(Month.NOVEMBER, 10, year);
            case JAMAICA: return getThird(DayOfWeek.MONDAY, Month.OCTOBER, year);
            case KENYA: return new EventDate(Month.OCTOBER, 20, year);
            case MALAYSIA: return new EventDate(Month.JULY, 31, year);
            case MOZAMBIQUE: return new EventDate(Month.FEBRUARY, 3, year);
            case NAMIBIA: return new EventDate(Month.AUGUST, 26, year);
            case PHILIPPINES: return getLast(DayOfWeek.MONDAY, Month.AUGUST, year);
            case RWANDA: return new EventDate(Month.FEBRUARY, 1, year);
            case SRI_LANKA: return new EventDate(Month.MAY, 22, year);
            case UGANDA: return new EventDate(Month.JUNE, 9, year);
            case UNITED_KINGDOM: return new EventDate(Month.OCTOBER, 21, year);
            case ZIMBABWE: return getSecond(DayOfWeek.MONDAY, Month.AUGUST, year);
            default: return null;
        }
    }
}
