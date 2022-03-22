package me.randomhashtags.worldlaws.observances.type;

import me.randomhashtags.worldlaws.observances.HolidayType;

public enum ChristianEastHoliday implements ChristianHoliday {
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

    @Override
    public HolidayType getType() {
        return HolidayType.CHRISTIAN_EAST;
    }

    @Override
    public boolean isWesternChristianity() {
        return false;
    }
}
