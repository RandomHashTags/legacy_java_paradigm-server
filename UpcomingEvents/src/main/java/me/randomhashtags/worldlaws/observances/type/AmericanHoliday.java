package me.randomhashtags.worldlaws.observances.type;

import me.randomhashtags.worldlaws.EventDate;
import me.randomhashtags.worldlaws.country.WLCountry;
import me.randomhashtags.worldlaws.observances.Holiday;
import me.randomhashtags.worldlaws.observances.HolidayType;

import java.time.DayOfWeek;
import java.time.Month;

public enum AmericanHoliday implements Holiday {

    ABRAHAM_LINCOLNS_BIRTHDAY(
            "Lincoln's Birthday"
    ),
    CHILD_HEALTH_DAY,
    //GOLD_STAR_MOTHERS_AND_FAMILYS_DAY(
    //        "Gold Star Mother's and Family's Day"
    //),
    HARRIET_TUBMAN_DAY,
    HELEN_KELLER_DAY,
    JUNETEENTH,
    LABOR_DAY,
    LAW_DAY,
    LEIF_ERIKSON_DAY,
    LOVING_DAY,
    LOYALTY_DAY,
    MALCOLM_X_DAY,
    MEMORIAL_DAY,
    MARTIN_LUTHER_KING_JR_DAY(
            "Martin Luther King Jr. Day"
    ),
    NATIONAL_AVIATION_DAY,
    NATIONAL_FREEDOM_DAY,
    NATIONAL_PEARL_HARBOR_REMEMBRANCE_DAY,
    PAN_AMERICAN_AVIATION_DAY,
    PATRIOTS_DAY(
            "Patriots' Day"
    ),
    ROSA_PARKS_DAY_ARRESTED(
            "Rosa Parks Day"
    ),
    ROSA_PARKS_DAY_BIRTHDAY(
            "Rosa Parks Day"
    ),

    THOMAS_JEFFERSON_DAY(
            "Jefferson's Birthday"
    ),

    WASHINGTONS_BIRTHDAY(
            "Washington's Birthday"
    ),
    WHITE_CANE_SAFETY_DAY,
    WRIGHT_BROTHERS_DAY,

    ;

    private final String wikipediaName;

    AmericanHoliday() {
        this(null);
    }
    AmericanHoliday(String wikipediaName) {
        this.wikipediaName = wikipediaName;
    }

    @Override
    public HolidayType getType() {
        return HolidayType.AMERICAN;
    }

    @Override
    public String getWikipediaName() {
        return wikipediaName;
    }

    @Override
    public String[] getAliases() {
        switch (this) {
            //case GOLD_STAR_MOTHERS_AND_FAMILYS_DAY: return new String[] { "Gold Star Mother's Day" };
            case JUNETEENTH: return collectAliases("Freedom Day", "Jubilee Day", "Liberation Day", "Emancipation Day");
            case PATRIOTS_DAY: return collectAliases("Patriot's Day");
            case MEMORIAL_DAY: return collectAliases("Decoration Day");
            case MARTIN_LUTHER_KING_JR_DAY: return collectAliases("Birthday of Martin Luther King, Jr.", "MLK Day");
            case WASHINGTONS_BIRTHDAY: return collectAliases("Presidents Day", "President's Day", "Presidents' Day");
            case WHITE_CANE_SAFETY_DAY: return collectAliases("Blind Americans Equality Day");
            default: return null;
        }
    }

    @Override
    public EventDate getDate(WLCountry country, int year) {
        switch (this) {
            case ABRAHAM_LINCOLNS_BIRTHDAY: return new EventDate(Month.FEBRUARY, 12, year);
            case CHILD_HEALTH_DAY: return EventDate.getFirst(DayOfWeek.MONDAY, Month.OCTOBER, year);
            //case GOLD_STAR_MOTHERS_AND_FAMILYS_DAY: return getLast(DayOfWeek.SUNDAY, Month.SEPTEMBER, year);
            case HARRIET_TUBMAN_DAY: return UnitedNationHoliday.INTERNATIONAL_WOMENS_DAY.getDate(country, year).plusDays(2);
            case HELEN_KELLER_DAY: return new EventDate(Month.JUNE, 27, year);
            case JUNETEENTH: return new EventDate(Month.JUNE, 19, year);
            case LABOR_DAY: return EventDate.getFirst(DayOfWeek.MONDAY, Month.SEPTEMBER, year);
            case LAW_DAY:
            case LOYALTY_DAY:
                return new EventDate(Month.MAY, 1, year);
            case LEIF_ERIKSON_DAY: return new EventDate(Month.OCTOBER, 9, year);
            case LOVING_DAY: return new EventDate(Month.JUNE, 12, year);
            case PAN_AMERICAN_AVIATION_DAY:
            case WRIGHT_BROTHERS_DAY:
                return new EventDate(Month.DECEMBER, 17, year);
            case PATRIOTS_DAY: return EventDate.getThird(DayOfWeek.MONDAY, Month.APRIL, year);
            case MALCOLM_X_DAY: return new EventDate(Month.MAY, 19, year);
            case MEMORIAL_DAY: return EventDate.getLast(DayOfWeek.MONDAY, Month.MAY, year);
            case NATIONAL_AVIATION_DAY: return new EventDate(Month.AUGUST, 19, year);
            case NATIONAL_FREEDOM_DAY: return new EventDate(Month.FEBRUARY, 1, year);
            case NATIONAL_PEARL_HARBOR_REMEMBRANCE_DAY: return new EventDate(Month.DECEMBER, 7, year);
            case MARTIN_LUTHER_KING_JR_DAY: return EventDate.getThird(DayOfWeek.MONDAY, Month.JANUARY, year);
            case ROSA_PARKS_DAY_ARRESTED: return new EventDate(Month.FEBRUARY, 4, year);
            case ROSA_PARKS_DAY_BIRTHDAY: return new EventDate(Month.DECEMBER, 1, year);
            case THOMAS_JEFFERSON_DAY: return new EventDate(Month.APRIL, 13, year);
            case WASHINGTONS_BIRTHDAY: return EventDate.getThird(DayOfWeek.MONDAY, Month.FEBRUARY, year);
            case WHITE_CANE_SAFETY_DAY: return new EventDate(Month.OCTOBER, 15, year);
        }
        return null;
    }
}
