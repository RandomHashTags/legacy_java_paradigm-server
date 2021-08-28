package me.randomhashtags.worldlaws.observances.type;

import me.randomhashtags.worldlaws.EventDate;
import me.randomhashtags.worldlaws.EventSource;
import me.randomhashtags.worldlaws.EventSources;
import me.randomhashtags.worldlaws.country.WLCountry;
import me.randomhashtags.worldlaws.observances.HolidaySource;
import me.randomhashtags.worldlaws.observances.IHoliday;

import java.time.DayOfWeek;
import java.time.Month;

public enum FunHoliday implements IHoliday { // https://www.timeanddate.com/holidays/fun/
    BOOK_LOVERS_DAY,
    DARWIN_DAY,
    FIBONACCI_DAY,
    INTERNATIONAL_BEER_DAY,
    INTERNATIONAL_DNA_DAY(
            "DNA Day"
    ),
    INTERNATIONAL_DOG_DAY,
    MOLE_DAY,
    NATIONAL_GIRLFRIEND_DAY,
    NATIONAL_SCIENCE_FICTION_DAY,
    PI_DAY,
    POLAR_BEAR_PLUNGE_DAY, // https://en.wikipedia.org/wiki/Polar_bear_plunge
    STAR_WARS_DAY,
    UGLY_SWEATER_DAY,
    WIKIPEDIA_DAY,
    WORLD_EMOJI_DAY,
    WORLD_LOGIC_DAY,
    WORLD_PHOTOGRAPHY_DAY,
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
        switch (this) {
            case INTERNATIONAL_BEER_DAY:
            case POLAR_BEAR_PLUNGE_DAY:
            case WIKIPEDIA_DAY:
            case WORLD_LOGIC_DAY:
                return HolidaySource.WIKIPEDIA;
            case INTERNATIONAL_DOG_DAY:
            case WORLD_PHOTOGRAPHY_DAY:
                return HolidaySource.OTHER;
            default:
                return HolidaySource.TIME_AND_DATE_FUN;
        }
    }

    @Override
    public String[] getAliases() {
        switch (this) {
            case BOOK_LOVERS_DAY: return collectAliases("National Book Lovers Day");
            case INTERNATIONAL_DNA_DAY: return collectAliases("International DNA Day", "National DNA Day", "World DNA Day");
            case POLAR_BEAR_PLUNGE_DAY: return collectAliases("New Year's Dive", "Polar Bear Swim Day");
            default: return null;
        }
    }

    @Override
    public EventSources getOtherSources() {
        switch (this) {
            case BOOK_LOVERS_DAY:
                return collectSources(
                        new EventSource("Time and Date", "https://www.timeanddate.com/holidays/fun/book-lovers-day")
                );
            case FIBONACCI_DAY:
                return collectSources(
                        new EventSource("Time and Date", "https://www.timeanddate.com/holidays/fun/fibonacci-day"),
                        new EventSource("National Today", "https://nationaltoday.com/fibonacci-day/")
                );
            case WORLD_PHOTOGRAPHY_DAY:
                return collectSources(
                        new EventSource("World Photography Day", "https://www.worldphotographyday.com")
                );
            default:
                return null;
        }
    }

    @Override
    public EventDate getDate(WLCountry country, int year) {
        switch (this) {
            case BOOK_LOVERS_DAY: return new EventDate(Month.AUGUST, 9, year);
            case DARWIN_DAY: return new EventDate(Month.FEBRUARY, 12, year);
            case FIBONACCI_DAY: return new EventDate(Month.NOVEMBER, 23, year);
            case INTERNATIONAL_BEER_DAY: return getFirst(DayOfWeek.FRIDAY, Month.AUGUST, year);
            case INTERNATIONAL_DNA_DAY: return new EventDate(Month.APRIL, 25, year);
            case INTERNATIONAL_DOG_DAY: return new EventDate(Month.AUGUST, 26, year);
            case MOLE_DAY: return new EventDate(Month.OCTOBER, 23, year);
            case NATIONAL_GIRLFRIEND_DAY: return country == WLCountry.UNITED_STATES ? new EventDate(Month.AUGUST, 1, year) : null;
            case NATIONAL_SCIENCE_FICTION_DAY: return country == WLCountry.UNITED_STATES ? new EventDate(Month.JANUARY, 2, year) : null;
            case PI_DAY: return new EventDate(Month.MARCH, 14, year);
            case POLAR_BEAR_PLUNGE_DAY:
                switch (country) {
                    case CANADA:
                    case UNITED_STATES:
                        return new EventDate(Month.JANUARY, 1, year);
                    default:
                        return null;
                }
            case STAR_WARS_DAY: return new EventDate(Month.MAY, 4, year);
            case UGLY_SWEATER_DAY: return getThird(DayOfWeek.FRIDAY, Month.DECEMBER, year);
            case WIKIPEDIA_DAY: return new EventDate(Month.JANUARY, 15, year);
            case WORLD_EMOJI_DAY: return new EventDate(Month.JULY, 17, year);
            case WORLD_LOGIC_DAY: return new EventDate(Month.JANUARY, 14, year);
            case WORLD_PHOTOGRAPHY_DAY: return new EventDate(Month.AUGUST, 19, year);
        }
        return null;
    }
}
