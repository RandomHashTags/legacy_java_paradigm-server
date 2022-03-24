package me.randomhashtags.worldlaws.observances.type;

import me.randomhashtags.worldlaws.EventDate;
import me.randomhashtags.worldlaws.country.WLCountry;
import me.randomhashtags.worldlaws.observances.Holiday;
import me.randomhashtags.worldlaws.observances.HolidayType;

import java.time.DayOfWeek;
import java.time.Month;

public enum ChristianHolidays implements Holiday {
    ALL_SAINTS_DAY,
    ALL_SOULS_DAY,
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
    SAINT_JOSEPHS_DAY,
    SAINT_PATRICKS_DAY,
    VALENTINES_DAY,
    WHIT_MONDAY
    ;

    public static ChristianHolidays valueOfString(String input) {
        for(ChristianHolidays holiday : values()) {
            if(holiday.name().equals(input)) {
                return holiday;
            }
        }
        return null;
    }

    @Override
    public HolidayType getType() {
        return null;
    }

    @Override
    public String getWikipediaName() {
        switch (this) {
            case ALL_SAINTS_DAY: return "All Saints' Day";
            case ALL_SOULS_DAY: return "All Souls' Day";
            case SAINT_JOSEPHS_DAY: return "Saint Joseph's Day";
            case SAINT_PATRICKS_DAY: return "Saint Patrick's Day";
            case VALENTINES_DAY: return "Valentine's Day";
            default: return null;
        }
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
                    final EventDate pentecost = PENTECOST.getDate(country, year);
                    return pentecost != null ? EventDate.getFirstAfter(DayOfWeek.SUNDAY, year, pentecost.getMonth(), pentecost.getDay()) : null;
                }
            case ALL_SOULS_DAY:
                return new EventDate(Month.NOVEMBER, 2, year);
            case ASH_WEDNESDAY:
                if(isWestern) {
                    final EventDate sevenWeeksBefore = ChristianHoliday.getEasterDatePlusDays(true, country, year, -7*7);
                    return EventDate.getFirstAfter(DayOfWeek.WEDNESDAY, year, sevenWeeksBefore.getMonth(), sevenWeeksBefore.getDay());
                } else {
                    return null;
                }
            case ASSUMPTION_OF_MARY:
                return new EventDate(Month.AUGUST, 15, year);
            case CHRISTMAS:
                return isWestern ? new EventDate(Month.DECEMBER, 25, year) : new EventDate(Month.JANUARY, 7, year);
            case CHRISTMAS_EVE:
                final EventDate christmas = CHRISTMAS.getDate(country, year);
                return christmas != null ? christmas.minusDays(1) : null;
            case EASTER:
                return ChristianHoliday.getEasterDate(isWestern, country, year);
            case EASTER_MONDAY:
                return ChristianHoliday.getEasterDatePlusDays(isWestern, country, year, 1);
            case EPIPHANY:
                final EventDate targetDate = new EventDate(Month.JANUARY, 6, year);
                if(isWestern) {
                    return targetDate;
                } else {
                    return targetDate.plusDays(ChristianHoliday.getDifferenceGregorianJulianDays(targetDate));
                }
            case FEAST_OF_THE_ASCENSION:
                return ChristianHoliday.getEasterDatePlusDays(isWestern, country, year, 39);
            case GOOD_FRIDAY:
                return ChristianHoliday.getEasterDatePlusDays(isWestern, country, year, -2);
            case HOLY_SATURDAY:
                return ChristianHoliday.getEasterDatePlusDays(isWestern, country, year, -1);
            case HOLY_TUESDAY:
                return ChristianHoliday.getEasterDatePlusDays(isWestern, country, year, -5);
            case HALLOWEEN:
                return new EventDate(Month.OCTOBER, 31, year);
            case IMMACULATE_CONCEPTION:
                return new EventDate(Month.DECEMBER, 8, year);
            case MARDI_GRAS:
                final EventDate ashWednesday = ASH_WEDNESDAY.getDate(country, year);
                return ashWednesday != null ? ashWednesday.minusDays(1) : null;
            case PALM_SUNDAY:
                return ChristianHoliday.getEasterDatePlusDays(isWestern, country, year, -7);
            case PENTECOST:
                return ChristianHoliday.getEasterDatePlusDays(isWestern, country, year, 7*7);
            case SAINT_JOSEPHS_DAY:
                return new EventDate(Month.MARCH, 19, year);
            case SAINT_PATRICKS_DAY:
                return new EventDate(Month.MARCH, 17, year);
            case VALENTINES_DAY:
                return isWestern ? new EventDate(Month.FEBRUARY, 14, year) : new EventDate(Month.JULY, 6, year);
            case WHIT_MONDAY:
                return ChristianHoliday.getEasterDatePlusDays(isWestern, country, year, 50);
            default:
                return null;
        }
    }
}
