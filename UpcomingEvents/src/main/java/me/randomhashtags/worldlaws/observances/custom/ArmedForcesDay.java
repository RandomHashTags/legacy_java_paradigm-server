package me.randomhashtags.worldlaws.observances.custom;

import me.randomhashtags.worldlaws.EventDate;
import me.randomhashtags.worldlaws.country.WLCountry;

import java.time.DayOfWeek;
import java.time.Month;

public enum ArmedForcesDay implements CustomIHoliday {
    INSTANCE;

    @Override
    public String getOfficialName() {
        return "Armed Forces Day";
    }

    @Override
    public EventDate getDate(WLCountry country, int year) {
        switch (country) {
            case AUSTRALIA:
            case COOK_ISLANDS:
            case NEW_ZEALAND:
            case TONGA:
                return new EventDate(Month.APRIL, 25, year);
            case AZERBAIJAN: return new EventDate(Month.JUNE, 26, year);
            case ARMENIA: return new EventDate(Month.JANUARY, 28, year);
            case BANGLADESH:
            case GREECE:
                return new EventDate(Month.NOVEMBER, 21, year);
            case BOLIVIA: return new EventDate(Month.AUGUST, 7, year);
            case BULGARIA: return new EventDate(Month.MAY, 6, year);
            case CANADA: return getFirst(DayOfWeek.SUNDAY, Month.JUNE, year);
            case CHILE: return new EventDate(Month.SEPTEMBER, 19, year);
            case CHINA:
            case LEBANON:
                return new EventDate(Month.AUGUST, 1, year);
            case CROATIA: return new EventDate(Month.MAY, 28, year);
            case CUBA: return new EventDate(Month.DECEMBER, 2, year);
            case DOMINICAN_REPUBLIC: return new EventDate(Month.FEBRUARY, 25, year);
            case EGYPT: return new EventDate(Month.OCTOBER, 6, year);
            case FINLAND: return new EventDate(Month.JUNE, 4, year);
            case GEORGIA: return new EventDate(Month.APRIL, 30, year);
            case GUATEMALA: return new EventDate(Month.JUNE, 30, year);
            case HAITI: return new EventDate(Month.NOVEMBER, 18, year);
            case HUNGARY: return new EventDate(Month.MAY, 21, year);
            case INDIA:
            case NIGERIA:
                return new EventDate(Month.JANUARY, 15, year);
            case INDONESIA: return new EventDate(Month.OCTOBER, 5, year);
            case IRAN: return new EventDate(Month.APRIL, 18, year);
            case IRAQ: return new EventDate(Month.JANUARY, 6, year);
            case ITALY: return new EventDate(Month.NOVEMBER, 4, year);
            case KAZAKHSTAN: return new EventDate(Month.MAY, 7, year);
            case LAOS: return new EventDate(Month.JANUARY, 20, year);
            case LATVIA: return new EventDate(Month.JULY, 10, year);
            case LIBERIA: return new EventDate(Month.FEBRUARY, 11, year);
            case LITHUANIA: return new EventDate(Month.NOVEMBER, 23, year);
            case MALAYSIA: return new EventDate(Month.SEPTEMBER, 16, year);
            case MALDIVES: return new EventDate(Month.APRIL, 21, year);
            case MALI: return new EventDate(Month.JANUARY, 20, year);
            case MEXICO: return new EventDate(Month.FEBRUARY, 19, year);
            case MONGOLIA: return new EventDate(Month.MARCH, 18, year);
            case MYANMAR: return new EventDate(Month.MARCH, 27, year);
            case NORTH_KOREA: return new EventDate(Month.FEBRUARY, 8, year);
            case NORTH_MACEDONIA: return new EventDate(Month.AUGUST, 18, year);
            case PAKISTAN: return new EventDate(Month.SEPTEMBER, 6, year);
            case PERU: return new EventDate(Month.SEPTEMBER, 24, year);
            case PHILIPPINES: return new EventDate(Month.DECEMBER, 21, year);
            case POLAND: return new EventDate(Month.AUGUST, 15, year);
            case ROMANIA: return new EventDate(Month.OCTOBER, 25, year);
            case RUSSIA: return new EventDate(Month.FEBRUARY, 23, year);
            case SERBIA: return new EventDate(Month.APRIL, 23, year);
            case SINGAPORE: return new EventDate(Month.JULY, 1, year);
            case SOUTH_AFRICA: return new EventDate(Month.FEBRUARY, 21, year);
            case SOUTH_KOREA: return new EventDate(Month.OCTOBER, 1, year);
            case TAIWAN: return new EventDate(Month.SEPTEMBER, 3, year);
            case TAJIKISTAN: return new EventDate(Month.FEBRUARY, 23, year);
            case THAILAND: return new EventDate(Month.JANUARY, 18, year);
            case TRANSNISTRIA: return new EventDate(Month.SEPTEMBER, 6, year);
            case UKRAINE: return new EventDate(Month.DECEMBER, 6, year);
            case UNITED_KINGDOM: return getLast(DayOfWeek.SATURDAY, Month.JUNE, year);
            case UNITED_STATES: return getThird(DayOfWeek.SUNDAY, Month.MAY, year);
            case VENEZUELA: return new EventDate(Month.JUNE, 24, year);
            case VIETNAM: return new EventDate(Month.DECEMBER, 22, year);
        }
        return null;
    }
}
