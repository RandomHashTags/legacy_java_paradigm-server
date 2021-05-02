package me.randomhashtags.worldlaws.observances.holidays;

import me.randomhashtags.worldlaws.EventDate;
import me.randomhashtags.worldlaws.location.WLCountry;

import java.time.DayOfWeek;
import java.time.Month;

public enum AmericanHoliday implements IHoliday {

    CHILD_HEALTH_DAY,
    HARRIET_TUBMAN_DAY,
    JUNETEENTH,
    LABOR_DAY,
    MALCOLM_X_DAY,
    MEMORIAL_DAY,
    MARTIN_LUTHER_KING_JR_DAY(
            "Martin Luther King Jr. Day"
    ),
    NATIONAL_AVIATION_DAY,
    ROSA_PARKS_DAY_ARRESTED(
            "Rosa Parks Day"
    ),
    ROSA_PARKS_DAY_BIRTHDAY(
            "Rosa Parks Day"
    ),

    WASHINGTONS_BIRTHDAY(
            "Washington's Birthday"
    ),

    ;

    private final String wikipediaName;

    AmericanHoliday() {
        this(null);
    }
    AmericanHoliday(String wikipediaName) {
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
            case JUNETEENTH: return new String[]{ "Freedom Day", "Jubilee Day", "Liberation Day", "Emancipation Day" };
            case MEMORIAL_DAY: return new String[] { "Decoration Day" };
            case MARTIN_LUTHER_KING_JR_DAY: return new String[] { "Birthday of Martin Luther King, Jr.", "MLK Day" };
            case WASHINGTONS_BIRTHDAY: return new String[] { "Presidents Day", "President's Day", "Presidents' Day" };
            default: return null;
        }
    }

    @Override
    public EventDate getDate(WLCountry country, int year) {
        switch (this) {
            case CHILD_HEALTH_DAY: return getFirst(DayOfWeek.MONDAY, Month.OCTOBER, year);
            case HARRIET_TUBMAN_DAY: return UnitedNationHoliday.INTERNATIONAL_WOMENS_DAY.getDate(country, year).plusDays(2);
            case JUNETEENTH: return new EventDate(Month.JUNE, 19, year);
            case LABOR_DAY: return getFirst(DayOfWeek.MONDAY, Month.SEPTEMBER, year);
            case MALCOLM_X_DAY: return new EventDate(Month.MAY, 19, year);
            case MEMORIAL_DAY: return getLast(DayOfWeek.MONDAY, Month.MAY, year);
            case NATIONAL_AVIATION_DAY: return new EventDate(Month.AUGUST, 19, year);
            case MARTIN_LUTHER_KING_JR_DAY: return getThird(DayOfWeek.MONDAY, Month.JANUARY, year);
            case ROSA_PARKS_DAY_ARRESTED: return new EventDate(Month.FEBRUARY, 4, year);
            case ROSA_PARKS_DAY_BIRTHDAY: return new EventDate(Month.DECEMBER, 1, year);
            case WASHINGTONS_BIRTHDAY: return getThird(DayOfWeek.MONDAY, Month.FEBRUARY, year);
        }
        return null;
    }
}
