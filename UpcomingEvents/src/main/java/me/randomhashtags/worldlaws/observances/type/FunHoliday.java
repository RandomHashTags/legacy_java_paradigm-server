package me.randomhashtags.worldlaws.observances.type;

import me.randomhashtags.worldlaws.EventDate;
import me.randomhashtags.worldlaws.EventSource;
import me.randomhashtags.worldlaws.EventSources;
import me.randomhashtags.worldlaws.country.WLCountry;
import me.randomhashtags.worldlaws.observances.Holiday;
import me.randomhashtags.worldlaws.observances.HolidaySource;
import me.randomhashtags.worldlaws.observances.HolidayType;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Month;

public enum FunHoliday implements Holiday { // https://www.timeanddate.com/holidays/fun/
    BOOK_LOVERS_DAY,
    DARWIN_DAY,
    FIBONACCI_DAY,
    INTERNATIONAL_BEER_DAY,
    INTERNATIONAL_DNA_DAY(
            "DNA Day"
    ),
    INTERNATIONAL_DOG_DAY,
    INTERNATIONAL_PROGRAMMERS_DAY,
    MOLE_DAY,
    NATIONAL_BOYFRIEND_DAY,
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
    public HolidayType getType() {
        return HolidayType.FUN;
    }

    @Override
    public String getWikipediaName() {
        return officialName;
    }

    @Override
    public HolidaySource getSource() {
        switch (this) {
            case INTERNATIONAL_BEER_DAY:
            case INTERNATIONAL_PROGRAMMERS_DAY:
            case NATIONAL_SCIENCE_FICTION_DAY:
            case WIKIPEDIA_DAY:
            case WORLD_LOGIC_DAY:
                return HolidaySource.WIKIPEDIA;
            case INTERNATIONAL_DOG_DAY:
            case WORLD_PHOTOGRAPHY_DAY:
                return null;
            default:
                return HolidaySource.TIME_AND_DATE;
        }
    }

    @Override
    public String[] getAliases() {
        switch (this) {
            case BOOK_LOVERS_DAY: return collectAliases("National Book Lovers Day");
            case INTERNATIONAL_DNA_DAY: return collectAliases("International DNA Day", "National DNA Day", "World DNA Day");
            case POLAR_BEAR_PLUNGE_DAY: return collectAliases("New Year's Dive", "Polar Bear Swim Day");
            case WORLD_PHOTOGRAPHY_DAY: return collectAliases("World Photo Day");
            default: return null;
        }
    }

    @Override
    public EventSources getSources(WLCountry country) {
        final String timeAndDate = "Time and Date";
        final String nationalToday = "National Today";
        switch (this) {
            case BOOK_LOVERS_DAY:
                return collectSources(
                        new EventSource(timeAndDate, "https://www.timeanddate.com/holidays/fun/book-lovers-day")
                );
            case FIBONACCI_DAY:
                return collectSources(
                        new EventSource(timeAndDate, "https://www.timeanddate.com/holidays/fun/fibonacci-day"),
                        new EventSource(nationalToday, "https://nationaltoday.com/fibonacci-day/")
                );
            case NATIONAL_BOYFRIEND_DAY:
                return collectSources(
                        new EventSource(nationalToday, "https://nationaltoday.com/national-boyfriend-day/")
                );
            case NATIONAL_GIRLFRIEND_DAY:
                return collectSources(
                        new EventSource(nationalToday, "https://nationaltoday.com/national-girlfriend-day/")
                );
            case WORLD_PHOTOGRAPHY_DAY:
                return collectSources(
                        new EventSource("World Photography Day", "https://www.worldphotographyday.com")
                );
            default:
                return new EventSources(new EventSource(timeAndDate + ": Fun Holidays", "https://www.timeanddate.com/holidays/fun/"));
        }
    }

    @Override
    public EventDate getDate(WLCountry country, int year) {
        switch (this) {
            case BOOK_LOVERS_DAY: return new EventDate(Month.AUGUST, 9, year);
            case DARWIN_DAY: return new EventDate(Month.FEBRUARY, 12, year);
            case FIBONACCI_DAY: return new EventDate(Month.NOVEMBER, 23, year);
            case INTERNATIONAL_BEER_DAY: return EventDate.getFirst(DayOfWeek.FRIDAY, Month.AUGUST, year);
            case INTERNATIONAL_DNA_DAY: return new EventDate(Month.APRIL, 25, year);
            case INTERNATIONAL_DOG_DAY: return new EventDate(Month.AUGUST, 26, year);
            case INTERNATIONAL_PROGRAMMERS_DAY:
                if(country == WLCountry.CHINA) {
                    return new EventDate(Month.OCTOBER, 24, year);
                } else {
                    final LocalDate date = LocalDate.ofYearDay(year, 256);
                    return new EventDate(date.getMonth(), date.getDayOfMonth(), year);
                }
            case MOLE_DAY: return new EventDate(Month.OCTOBER, 23, year);
            case NATIONAL_BOYFRIEND_DAY: return new EventDate(Month.OCTOBER, 3, year);
            case NATIONAL_GIRLFRIEND_DAY: return new EventDate(Month.AUGUST, 1, year);
            case NATIONAL_SCIENCE_FICTION_DAY: return country == WLCountry.UNITED_STATES ? new EventDate(Month.JANUARY, 2, year) : null;
            case PI_DAY: return new EventDate(Month.MARCH, 14, year);
            case POLAR_BEAR_PLUNGE_DAY:
                if(country == null) {
                    return null;
                }
                switch (country) {
                    case CANADA:
                    case UNITED_STATES:
                        return new EventDate(Month.JANUARY, 1, year);
                    default:
                        return null;
                }
            case STAR_WARS_DAY: return new EventDate(Month.MAY, 4, year);
            case UGLY_SWEATER_DAY: return EventDate.getThird(DayOfWeek.FRIDAY, Month.DECEMBER, year);
            case WIKIPEDIA_DAY: return new EventDate(Month.JANUARY, 15, year);
            case WORLD_EMOJI_DAY: return new EventDate(Month.JULY, 17, year);
            case WORLD_LOGIC_DAY: return new EventDate(Month.JANUARY, 14, year);
            case WORLD_PHOTOGRAPHY_DAY: return new EventDate(Month.AUGUST, 19, year);
            default: return null;
        }
    }
}
