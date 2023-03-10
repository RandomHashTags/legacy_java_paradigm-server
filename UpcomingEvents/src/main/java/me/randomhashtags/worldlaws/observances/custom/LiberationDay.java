package me.randomhashtags.worldlaws.observances.custom;

import me.randomhashtags.worldlaws.EventDate;
import me.randomhashtags.worldlaws.country.WLCountry;

import java.time.DayOfWeek;
import java.time.Month;

public enum LiberationDay implements CustomHoliday {
    INSTANCE;

    @Override
    public String getWikipediaName() {
        return "Liberation Day";
    }

    @Override
    public EventDate getDate(WLCountry country, int year) {
        switch (country) {
            case ALBANIA: return new EventDate(Month.NOVEMBER, 29, year);
            case BULGARIA:
            case UNITED_STATES:
                return new EventDate(Month.MARCH, 3, year);
            case DEMOCRATIC_REPUBLIC_OF_THE_CONGO: return new EventDate(Month.MAY, 17, year);
            case CUBA: return new EventDate(Month.JANUARY, 1, year);
            case CZECH_REPUBLIC:
            case NORWAY:
            case SLOVAKIA:
                return new EventDate(Month.MAY, 8, year);
            case DENMARK:
            case NETHERLANDS:
                return new EventDate(Month.MAY, 5, year);
            case FALKLAND_ISLANDS: return new EventDate(Month.JUNE, 14, year);
            case GUERNSEY:
            case JERSEY:
                return new EventDate(Month.MAY, 9, year);
            case HUNGARY: return new EventDate(Month.APRIL, 4, year);
            case HONG_KONG: return EventDate.getLast(DayOfWeek.MONDAY, Month.AUGUST, year);
            case INDIA: return new EventDate(Month.DECEMBER, 19, year);
            case ITALY: return new EventDate(Month.APRIL, 25, year);
            case KUWAIT: return new EventDate(Month.FEBRUARY, 26, year);
            case LEBANON: return new EventDate(Month.MAY, 25, year);
            case LIBYA: return new EventDate(Month.OCTOBER, 23, year);
            case NORTH_MACEDONIA: return new EventDate(Month.OCTOBER, 11, year);
            case MALI: return new EventDate(Month.NOVEMBER, 19, year);
            case MOLDOVA: return new EventDate(Month.AUGUST, 24, year);
            case NICARAGUA: return new EventDate(Month.JULY, 19, year);
            case NORTH_KOREA:
            case SOUTH_KOREA:
                return new EventDate(Month.AUGUST, 15, year);
            case RWANDA: return new EventDate(Month.JULY, 4, year);
            case TOGO: return new EventDate(Month.JANUARY, 13, year);
            case TRANSNISTRIA: return new EventDate(Month.APRIL, 12, year);
            case UGANDA: return new EventDate(Month.JANUARY, 26, year);
            case UKRAINE: return new EventDate(Month.OCTOBER, 28, year);
            case VIETNAM: return new EventDate(Month.APRIL, 30, year);
        }
        return null;
    }
}
