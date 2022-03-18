package me.randomhashtags.worldlaws.observances.type;

import me.randomhashtags.worldlaws.EventDate;
import me.randomhashtags.worldlaws.country.WLCountry;
import me.randomhashtags.worldlaws.observances.Holiday;
import me.randomhashtags.worldlaws.observances.HolidayType;

import java.time.DayOfWeek;
import java.time.Month;

public enum ChristianHoliday implements Holiday {

    ALL_SAINTS_DAY(
            "All Saints' Day"
    ),
    ALL_SOULS_DAY(
            "All Souls' Day"
    ),
    ASH_WEDNESDAY,
    ASSUMPTION_OF_MARY,
    CHRISTMAS,
    CHRISTMAS_EVE,
    EASTER,
    EASTER_MONDAY,
    EPIPHANY,
    FEAST_OF_THE_ASCENSION,
    GOOD_FRIDAY,
    HALLOWEEN,
    HOLY_SATURDAY,
    HOLY_TUESDAY,
    IMMACULATE_CONCEPTION,
    MARDI_GRAS,
    PALM_SUNDAY,
    PENTECOST,
    SAINT_JOSEPHS_DAY(
            "Saint Joseph's Day"
    ),
    SAINT_PATRICKS_DAY(
            "Saint Patrick's Day"
    ),
    VALENTINES_DAY(
            "Valentine's Day"
    ),
    WHIT_MONDAY

    ;

    private final String wikipediaName;

    ChristianHoliday() {
        this(null);
    }
    ChristianHoliday(String wikipediaName) {
        this.wikipediaName = wikipediaName;
    }

    @Override
    public HolidayType getType() {
        return null; // TODO: fix this
    }

    @Override
    public String getWikipediaName() {
        return wikipediaName;
    }

    @Override
    public String[] getAliases() {
        switch (this) {
            case ALL_SAINTS_DAY: return collectAliases("All Hallows' Day", "Hallowmas", "Feast of All Saints", "Solemnity of All Saints");
            case ALL_SOULS_DAY: return collectAliases("Commemoration of All the Faithful Departed", "Day of Remembrance", "Day of the Dead", "Feast of All Souls");
            case ASSUMPTION_OF_MARY: return collectAliases("Assumption of the Blessed Virgin Mary", "Falling Asleep of the Blessed Virgin Mary", "The Assumption");
            case CHRISTMAS: return collectAliases("Feast of the Nativity", "Noël", "Xmas");
            case CHRISTMAS_EVE: return collectAliases("Day before Christmas", "Vigil of Christmas", "Night before Christmas");
            case EASTER: return collectAliases("Pascha", "Resurrection Sunday");
            case EPIPHANY: return collectAliases("Baptism of Jesus", "Little Christmas", "Three Kings Day", "Theophany");
            case FEAST_OF_THE_ASCENSION: return collectAliases("Feast of the Ascension of Jesus Christ", "Ascension Day", "Ascension Thursday", "Holy Thursday");
            case GOOD_FRIDAY: return collectAliases("Crucified Friday", "Holy Friday", "Great Friday", "Great and Holy Friday", "Black Friday");
            case HALLOWEEN: return collectAliases("Allhalloween", "All Hallows' Eve", "All Saints' Eve", "Hallowe'en");
            case HOLY_SATURDAY: return collectAliases("Black Saturday", "Easter Eve", "Great and Holy Saturday", "Great Sabbath", "Hallelujah Saturday", "Holy and Great Saturday", "Joyous Saturday", "Saturday of the Gloria", "the Saturday of Light");
            case HOLY_TUESDAY: return collectAliases("Fig Tuesday", "Great and Holy Tuesday", "Great Tuesday");
            case MARDI_GRAS: return collectAliases("Fat Tuesday", "Pancake Tuesday", "Shrove Tuesday");
            case PENTECOST: return collectAliases("Trinity Sunday", "White Sunday", "Whitsunday", "Whitsun");
            case SAINT_JOSEPHS_DAY: return collectAliases("Feast of Saint Joseph", "Solemnity of Saint Joseph");
            case SAINT_PATRICKS_DAY: return collectAliases("Feast of Saint Patrick", "Patrick's Day", "St. Paddy's Day", "St. Patty's Day");
            case VALENTINES_DAY: return collectAliases("Feast of Saint Valentine", "Saint Valentine's Day");
            case WHIT_MONDAY: return collectAliases("Memorial of the Blessed Virgin Mary, Mother of the Church", "Monday of the Holy Spirit", "Pentecost Monday");
            default: return null;
        }
    }

    @Override
    public EventDate getDate(WLCountry country, int year) {
        return null;
    }

    public EventDate getDate(boolean isWestern, WLCountry country, int year) {
        switch (this) {
            case ALL_SAINTS_DAY:
                if(isWestern) {
                    return new EventDate(Month.NOVEMBER, 1, year);
                } else {
                    final EventDate pentecost = PENTECOST.getDate(false, country, year);
                    return pentecost != null ? EventDate.getFirstAfter(DayOfWeek.SUNDAY, year, pentecost.getMonth(), pentecost.getDay()) : null;
                }
            case ALL_SOULS_DAY:
                return new EventDate(Month.NOVEMBER, 2, year);
            case ASH_WEDNESDAY:
                if(isWestern) {
                    final EventDate sevenWeeksBefore = getEasterDatePlusDays(true, country, year, -7*7);
                    return sevenWeeksBefore != null ? EventDate.getFirstAfter(DayOfWeek.WEDNESDAY, year, sevenWeeksBefore.getMonth(), sevenWeeksBefore.getDay()) : null;
                }
                return null;
            case ASSUMPTION_OF_MARY:
                return new EventDate(Month.AUGUST, 15, year);
            case CHRISTMAS:
                return isWestern ? new EventDate(Month.DECEMBER, 25, year) : new EventDate(Month.JANUARY, 7, year);
            case CHRISTMAS_EVE:
                final EventDate christmas = CHRISTMAS.getDate(isWestern, country, year);
                return christmas != null ? christmas.minusDays(1) : null;
            case EASTER: // https://en.wikipedia.org/wiki/Computus#Algorithms
                if(isWestern) {
                    return getAnonymousGregorianEaster(year);
                } else {
                    final EventDate oldStyleDate = getMeeusJulianEaster(year);
                    return oldStyleDate.plusDays(getDifferenceGregorianJulianDays(oldStyleDate));
                }
            case EASTER_MONDAY:
                return getEasterDatePlusDays(isWestern, country, year, 1);
            case EPIPHANY:
                final EventDate targetDate = new EventDate(Month.JANUARY, 6, year);
                return isWestern ? targetDate : targetDate.minusDays(getDifferenceGregorianJulianDays(targetDate));
            case FEAST_OF_THE_ASCENSION:
                return getEasterDatePlusDays(isWestern, country, year, 39);
            case GOOD_FRIDAY:
                return getEasterDatePlusDays(isWestern, country, year, -2);
            case HALLOWEEN:
                return new EventDate(Month.OCTOBER, 31, year);
            case HOLY_SATURDAY:
                return getEasterDatePlusDays(isWestern, country, year, -1);
            case HOLY_TUESDAY:
                return getEasterDatePlusDays(isWestern, country, year, -5);
            case IMMACULATE_CONCEPTION:
                return new EventDate(Month.DECEMBER, 8, year);
            case MARDI_GRAS:
                final EventDate ashWednesday = ASH_WEDNESDAY.getDate(isWestern, country, year);
                return ashWednesday != null ? ashWednesday.minusDays(1) : null;
            case PALM_SUNDAY:
                return getEasterDatePlusDays(isWestern, country, year, -7);
            case PENTECOST:
                return getEasterDatePlusDays(isWestern, country, year, 7*7);
            case SAINT_JOSEPHS_DAY:
                return new EventDate(Month.MARCH, 19, year);
            case SAINT_PATRICKS_DAY:
                return new EventDate(Month.MARCH, 17, year);
            case VALENTINES_DAY:
                return isWestern ? new EventDate(Month.FEBRUARY, 14, year) : new EventDate(Month.JULY, 6, year);
            case WHIT_MONDAY:
                return getEasterDatePlusDays(isWestern, country, year, 50);
            default:
                return null;
        }
    }
    private EventDate getEasterDatePlusDays(boolean isWestern, WLCountry country, int year, long days) {
        final EventDate easter = EASTER.getDate(isWestern, country, year);
        return easter != null ? easter.plusDays(days) : null;
    }

    private EventDate getAnonymousGregorianEaster(int year) {
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
    private EventDate getMeeusJulianEaster(int year) {
        final int a = year % 4, b = year % 7, c = year % 19;
        final int d = ((19*c) + 15) % 30;
        final int e = ((2*a) + (4*b) - d + 34) % 7;
        final int month = (d + e + 114) / 31;
        final int day = ((d + e + 114) % 31) + 1;
        final Month julianMonth = Month.of(month);
        return new EventDate(julianMonth, day, year);
    }
    public static int getDifferenceGregorianJulianDays(EventDate date) {
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
