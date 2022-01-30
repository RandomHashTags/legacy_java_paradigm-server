package me.randomhashtags.worldlaws.observances.type;

import me.randomhashtags.worldlaws.EventDate;
import me.randomhashtags.worldlaws.country.WLCountry;
import me.randomhashtags.worldlaws.observances.HolidaySource;
import me.randomhashtags.worldlaws.observances.IHoliday;
import me.randomhashtags.worldlaws.observances.custom.NationalDonutDay;

import java.time.DayOfWeek;
import java.time.Month;

public enum UnofficialHoliday implements IHoliday {
    // https://en.wikipedia.org/wiki/Category:International_observances
    // https://en.wikipedia.org/wiki/List_of_minor_secular_observances

    APRIL_FOOLS_DAY(
            "April Fools' Day"
    ),
    DISASTER_PREVENTION_DAY,
    EVOLUTION_DAY,
    GALACTIC_TICK_DAY,
    INTERNATIONAL_MENS_DAY(
            "International Men's Day"
    ),
    NATIONAL_CASHEW_DAY,
    NATIONAL_DONUT_DAY,
    NATIONAL_HONEY_BEE_DAY,
    NATIONAL_INTERN_DAY,
    NATIONAL_NUDE_DAY,
    NATIONAL_PUZZLE_DAY,
    NATIONAL_SHOWER_WITH_A_FRIEND_DAY,
    SPIRIT_DAY,
    SQUARE_ROOT_DAY,
    SUPER_BOWL_SUNDAY,
    WEAR_IT_PURPLE_DAY,
    WORLD_CONTACT_DAY,
    ;

    private final String wikipediaName;

    UnofficialHoliday() {
        this(null);
    }
    UnofficialHoliday(String wikipediaName) {
        this.wikipediaName = wikipediaName;
    }

    @Override
    public Enum<? extends IHoliday> getEnum() {
        return this;
    }

    @Override
    public String getOfficialName() {
        return wikipediaName;
    }

    @Override
    public String[] getAliases() {
        switch (this) {
            case APRIL_FOOLS_DAY: return collectAliases("April Fool's Day");
            case INTERNATIONAL_MENS_DAY: return collectAliases("IMD", "Men's Day");
            case NATIONAL_DONUT_DAY: return collectAliases("National Doughnut Day");
            case SUPER_BOWL_SUNDAY: return collectAliases("Super Sunday");
            default: return null;
        }
    }

    @Override
    public EventDate getDate(WLCountry country, int year) {
        switch (this) {
            case APRIL_FOOLS_DAY: return new EventDate(Month.APRIL, 1, year);
            case DISASTER_PREVENTION_DAY:
                return country == WLCountry.JAPAN ? new EventDate(Month.SEPTEMBER, 1, year) : null;
            case EVOLUTION_DAY: return new EventDate(Month.NOVEMBER, 24, year);
            case GALACTIC_TICK_DAY: return getGalacticTickDay(year);
            case INTERNATIONAL_MENS_DAY:
                return new EventDate(Month.NOVEMBER, 19, year);

            case NATIONAL_CASHEW_DAY:
                if(country != null) {
                    switch (country) {
                        case UNITED_STATES: return new EventDate(Month.NOVEMBER, 23, year);
                        default: return null;
                    }
                } else {
                    return null;
                }
            case NATIONAL_DONUT_DAY: return NationalDonutDay.INSTANCE.getDate(country, year);
            case NATIONAL_HONEY_BEE_DAY: return country == WLCountry.UNITED_STATES ? getThird(DayOfWeek.SATURDAY, Month.AUGUST, year) : null;
            case NATIONAL_INTERN_DAY: return country == WLCountry.UNITED_STATES ? getLast(DayOfWeek.THURSDAY, Month.JULY, year) : null;
            case NATIONAL_NUDE_DAY: return new EventDate(Month.JULY, 14, year);
            case NATIONAL_PUZZLE_DAY: return country == WLCountry.UNITED_STATES ? new EventDate(Month.JANUARY, 29, year) : null;
            case NATIONAL_SHOWER_WITH_A_FRIEND_DAY: return country == WLCountry.UNITED_STATES ? new EventDate(Month.FEBRUARY, 5, year) : null;

            case SPIRIT_DAY:
                if(country != null) {
                    switch (country) {
                        case CANADA:
                        case UNITED_STATES:
                            return getThird(DayOfWeek.THURSDAY, Month.OCTOBER, year);
                        default:
                            return null;
                    }
                } else {
                    return null;
                }
            case SQUARE_ROOT_DAY:
                String string = Integer.toString(year);
                string = string.substring(string.length()-2);
                switch (string) {
                    case "01": return new EventDate(Month.JANUARY, 1, year);
                    case "04": return new EventDate(Month.FEBRUARY, 2, year);
                    case "09": return new EventDate(Month.MARCH, 3, year);
                    case "16": return new EventDate(Month.APRIL, 4, year);
                    case "25": return new EventDate(Month.MAY, 5, year);
                    case "36": return new EventDate(Month.JUNE, 6, year);
                    case "49": return new EventDate(Month.JULY, 7, year);
                    case "64": return new EventDate(Month.AUGUST, 8, year);
                    case "81": return new EventDate(Month.SEPTEMBER, 9, year);
                    default: return null;
                }
            case SUPER_BOWL_SUNDAY:
                return year <= 2021 ? getFirst(DayOfWeek.SUNDAY, Month.FEBRUARY, year) : getSecond(DayOfWeek.SUNDAY, Month.FEBRUARY, year);
            case WEAR_IT_PURPLE_DAY:
                return country == WLCountry.AUSTRALIA ? getLast(DayOfWeek.FRIDAY, Month.AUGUST, year) : null;
            case WORLD_CONTACT_DAY: return new EventDate(Month.MARCH, 15, year);
        }
        return null;
    }

    private EventDate getGalacticTickDay(int year) {
        final EventDate startDate = new EventDate(Month.OCTOBER, 2, 1608);
        final float intervalYears = 1.7361f, intervalDays = intervalYears*365;
        for(int i = 1; i <= 5000; i++) {
            final int days = (int) (i*intervalDays);
            final EventDate targetDate = startDate.plusDays(days);
            final int targetYear = targetDate.getYear();
            if(targetYear == year) {
                return targetDate;
            }
        }
        return null;
    }

    @Override
    public HolidaySource getSource() {
        switch (this) {
            case NATIONAL_PUZZLE_DAY:
            case NATIONAL_SHOWER_WITH_A_FRIEND_DAY:
                return HolidaySource.NATIONAL_TODAY;
            default:
                return HolidaySource.WIKIPEDIA;
        }
    }
}
