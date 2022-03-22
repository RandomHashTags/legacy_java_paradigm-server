package me.randomhashtags.worldlaws.observances.type;

import me.randomhashtags.worldlaws.EventDate;
import me.randomhashtags.worldlaws.country.WLCountry;
import me.randomhashtags.worldlaws.observances.Holiday;

import java.time.Month;

public interface ChristianHoliday extends Holiday {
    boolean isWesternChristianity();
    static EventDate getEasterDate(boolean isWestern, WLCountry country, int year) {
        return isWestern ? getWesternEaster(year) : getEasternEaster(year);
    }
    static EventDate getEasterDatePlusDays(boolean isWestern, WLCountry country, int year, long days) {
        final EventDate easter = getEasterDate(isWestern, country, year);
        return easter.plusDays(days);
    }

    @Override
    default String getWikipediaName() {
        final ChristianHolidays test = ChristianHolidays.valueOfString(name());
        return test != null ? test.getWikipediaName() : null;
    }

    @Override
    default String[] getAliases() {
        final ChristianHolidays test = ChristianHolidays.valueOfString(name());
        return test != null ? test.getAliases() : null;
    }

    @Override
    default EventDate getDate(WLCountry country, int year) {
        final ChristianHolidays test = ChristianHolidays.valueOfString(name());
        return test != null ? test.getDate(isWesternChristianity(), country, year) : null;
    }

    private static EventDate getWesternEaster(int year) {
        final int a = year % 19, b = year / 100, c = year % 100, d = b / 4, e = b % 4;
        final int g = ((8*b) + 13) / 25;
        final int h = ((19*a) + b - d - g + 15) % 30;
        final int i = c / 4, k = c % 4;
        final int l = (32 + (2*e) + (2*i) - h - k) % 7;
        final int m = (a + (11*h) + (19*l)) / 433;
        final int month = (h + l - (7*m) + 90) / 25;
        final int day = (h + l - (7*m) + (33*month) + 19) % 32;
        final Month gregorianMonth = Month.of(month);
        return new EventDate(gregorianMonth, day, year);
    }
    private static EventDate getEasternEaster(int year) {
        final EventDate oldStyleDate = getMeeusJulianEaster(year);
        return oldStyleDate.plusDays(getDifferenceGregorianJulianDays(oldStyleDate));
    }
    private static EventDate getMeeusJulianEaster(int year) {
        final int a = year % 4, b = year % 7, c = year % 19;
        final int d = ((19*c) + 15) % 30;
        final int e = ((2*a) + (4*b) - d + 34) % 7;
        final int month = (d + e + 114) / 31;
        final int day = ((d + e + 114) % 31) + 1;
        final Month julianMonth = Month.of(month);
        return new EventDate(julianMonth, day, year);
    }
    static int getDifferenceGregorianJulianDays(EventDate date) {
        final int year = date.getYear(), day = date.getDay();
        final Month month = date.getMonth();
        final int difference = (year/100) - (year/400) - 2;
        if(year % 100 == 0) { // starting 15 October, 1582 | https://en.wikipedia.org/wiki/Gregorian_calendar
            switch (month) {
                case JANUARY:
                    return difference-1;
                case FEBRUARY:
                    return day <= 28 ? difference-1 : difference;
                default:
                    return difference;
            }
        } else {
            return difference;
        }
    }
}
