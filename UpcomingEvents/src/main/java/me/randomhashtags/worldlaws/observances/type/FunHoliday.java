package me.randomhashtags.worldlaws.observances.type;

import me.randomhashtags.worldlaws.EventDate;
import me.randomhashtags.worldlaws.country.WLCountry;
import me.randomhashtags.worldlaws.observances.HolidaySource;
import me.randomhashtags.worldlaws.observances.IHoliday;

import java.time.Month;

public enum FunHoliday implements IHoliday { // https://www.timeanddate.com/holidays/fun/
    DARWIN_DAY,
    INTERNATIONAL_DNA_DAY(
            "DNA Day"
    ),
    MOLE_DAY,
    NATIONAL_GIRLFRIEND_DAY,
    PI_DAY,
    STAR_WARS_DAY,
    WORLD_EMOJI_DAY,
    ;

    private final String officialName;

    FunHoliday() {
        this(null);
    }
    FunHoliday(String wikipediaName) {
        this.officialName = wikipediaName;
    }

    @Override
    public Enum<? extends IHoliday> getEnum() {
        return this;
    }

    @Override
    public String getOfficialName() {
        return officialName;
    }

    @Override
    public HolidaySource getSource() {
        return HolidaySource.TIME_AND_DATE_FUN;
    }

    @Override
    public String[] getAliases() {
        switch (this) {
            case INTERNATIONAL_DNA_DAY: return new String[] { "International DNA Day", "National DNA Day", "World DNA Day" };
            default: return null;
        }
    }

    @Override
    public EventDate getDate(WLCountry country, int year) {
        switch (this) {
            case DARWIN_DAY: return new EventDate(Month.FEBRUARY, 12, year);
            case INTERNATIONAL_DNA_DAY: return new EventDate(Month.APRIL, 25, year);
            case MOLE_DAY: return new EventDate(Month.OCTOBER, 23, year);
            case NATIONAL_GIRLFRIEND_DAY: return country == WLCountry.UNITED_STATES ? new EventDate(Month.AUGUST, 1, year) : null;
            case PI_DAY: return new EventDate(Month.MARCH, 14, year);
            case STAR_WARS_DAY: return new EventDate(Month.MAY, 4, year);
            case WORLD_EMOJI_DAY: return new EventDate(Month.JULY, 17, year);
        }
        return null;
    }
}
