package me.randomhashtags.worldlaws.observances.type;

import me.randomhashtags.worldlaws.EventDate;
import me.randomhashtags.worldlaws.EventSource;
import me.randomhashtags.worldlaws.EventSources;
import me.randomhashtags.worldlaws.country.WLCountry;
import me.randomhashtags.worldlaws.observances.IHoliday;

import java.time.DayOfWeek;
import java.time.Month;

public enum UnitedNationHoliday implements IHoliday {
    // https://en.wikipedia.org/wiki/Category:United_Nations_days

    EARTH_DAY,

    GLOBAL_DAY_OF_PARENTS(
            "Parent's Day"
    ),
    GLOBAL_HANDWASHING_DAY,

    HUMAN_RIGHTS_DAY,

    INTERNATIONAL_DANCE_DAY,
    INTERNATIONAL_DAY_OF_DEMOCRACY,
    INTERNATIONAL_DAY_OF_EDUCATION,
    INTERNATIONAL_DAY_OF_FAMILIES,
    INTERNATIONAL_DAY_OF_FORESTS,
    INTERNATIONAL_DAY_OF_HAPPINESS,
    INTERNATIONAL_DAY_OF_HUMAN_SPACE_FLIGHT,
    INTERNATIONAL_DAY_OF_LIGHT,
    INTERNATIONAL_DAY_OF_PEACE,
    INTERNATIONAL_DAY_OF_YOGA,
    INTERNATIONAL_FIREFIGHTERS_DAY(
            "International Firefighters' Day"
    ),
    INTERNATIONAL_HOLOCAUST_REMEMBRANCE_DAY,
    INTERNATIONAL_HUMAN_SOLIDARITY_DAY,
    INTERNATIONAL_LITERACY_DAY,
    INTERNATIONAL_MOTHER_EARTH_DAY,
    INTERNATIONAL_NURSES_DAY(
            "International Nurses Day"
    ),
    INTERNATIONAL_VOLUNTEER_DAY,
    INTERNATIONAL_WIDOWS_DAY,
    INTERNATIONAL_WOMENS_DAY(
            "International Women's Day"
    ),
    INTERNATIONAL_YOUTH_DAY,

    NATIONAL_CLEANUP_DAY(
            "National CleanUp Day"
    ),
    NATIONAL_WORLD_BOOK_DAY(
            "World Book Day"
    ),

    UNITED_NATIONS_DAY,

    WORLD_AIDS_DAY(
            "World AIDS Day"
    ),
    WORLD_AUTISM_AWARENESS_DAY,
    WORLD_BEE_DAY,
    WORLD_BICYCLE_DAY,
    WORLD_BLOOD_DONOR_DAY,
    WORLD_BRAILLE_DAY,
    WORLD_CANCER_DAY,
    WORLD_CITIES_DAY,
    WORLD_CREATIVITY_AND_INNOVATION_DAY,
    WORLD_DEVELOPMENT_INFORMATION_DAY,
    WORLD_DOWN_SYNDROME_DAY,
    WORLD_ENVIRONMENT_DAY,
    WORLD_FOOD_DAY,
    WORLD_HABITAT_DAY,
    WORLD_HEALTH_DAY,
    WORLD_HEPATITIS_DAY,
    WORLD_HUMANITARIAN_DAY,
    WORLD_LOGIC_DAY,
    WORLD_MALARIA_DAY,
    WORLD_NO_TOBACCO_DAY,
    WORLD_POETRY_DAY,
    WORLD_PHILOSOPHY_DAY,
    WORLD_PRESS_FREEDOM_DAY,
    WORLD_RADIO_DAY,
    WORLD_TOILET_DAY,
    WORLD_TOURISM_DAY,
    WORLD_WATER_DAY,
    WORLD_WILDLIFE_DAY,

    ZERO_DISCRIMINATION_DAY,
    ;

    private final String wikipediaName;

    UnitedNationHoliday() {
        this(null);
    }
    UnitedNationHoliday(String wikipediaName) {
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
            case INTERNATIONAL_DAY_OF_LIGHT: return collectAliases("Light Day");
            default: return null;
        }
    }

    @Override
    public EventDate getDate(WLCountry country, int year) {
        switch (this) {
            case EARTH_DAY:
            case INTERNATIONAL_MOTHER_EARTH_DAY:
                return new EventDate(Month.APRIL, 22, year);
            case GLOBAL_DAY_OF_PARENTS: return new EventDate(Month.JUNE, 1, year);
            case GLOBAL_HANDWASHING_DAY: return new EventDate(Month.OCTOBER, 15, year);
            case HUMAN_RIGHTS_DAY: return new EventDate(Month.DECEMBER, 10, year);
            case INTERNATIONAL_DANCE_DAY: return new EventDate(Month.APRIL, 29, year);
            case INTERNATIONAL_DAY_OF_DEMOCRACY: return new EventDate(Month.SEPTEMBER, 15, year);
            case INTERNATIONAL_DAY_OF_EDUCATION: return new EventDate(Month.JANUARY, 24, year);
            case INTERNATIONAL_DAY_OF_FAMILIES: return new EventDate(Month.MAY, 15, year);
            case INTERNATIONAL_DAY_OF_FORESTS:
            case WORLD_DOWN_SYNDROME_DAY:
                return new EventDate(Month.MARCH, 21, year);
            case INTERNATIONAL_DAY_OF_HAPPINESS: return new EventDate(Month.MARCH, 20, year);
            case INTERNATIONAL_DAY_OF_HUMAN_SPACE_FLIGHT: return new EventDate(Month.APRIL, 12, year);
            case INTERNATIONAL_DAY_OF_LIGHT: return new EventDate(Month.MAY, 16, year);
            case INTERNATIONAL_DAY_OF_PEACE: return new EventDate(Month.SEPTEMBER, 21, year);
            case INTERNATIONAL_DAY_OF_YOGA: return new EventDate(Month.JUNE, 21, year);
            case INTERNATIONAL_FIREFIGHTERS_DAY: return new EventDate(Month.MAY, 4, year);
            case INTERNATIONAL_HOLOCAUST_REMEMBRANCE_DAY: return new EventDate(Month.JANUARY, 27, year);
            case INTERNATIONAL_HUMAN_SOLIDARITY_DAY: return new EventDate(Month.DECEMBER, 20, year);
            case INTERNATIONAL_LITERACY_DAY: return new EventDate(Month.SEPTEMBER, 8, year);

            case INTERNATIONAL_NURSES_DAY: return new EventDate(Month.MAY, 12, year);
            case INTERNATIONAL_VOLUNTEER_DAY: return new EventDate(Month.DECEMBER, 5, year);
            case INTERNATIONAL_WIDOWS_DAY: return new EventDate(Month.JUNE, 23, year);
            case INTERNATIONAL_WOMENS_DAY: return new EventDate(Month.MARCH, 8, year);
            case INTERNATIONAL_YOUTH_DAY: return new EventDate(Month.AUGUST, 12, year);
            case NATIONAL_CLEANUP_DAY: return getThird(DayOfWeek.SATURDAY, Month.SEPTEMBER, year);
            case NATIONAL_WORLD_BOOK_DAY: return new EventDate(Month.APRIL, 23, year);
            case UNITED_NATIONS_DAY:
            case WORLD_DEVELOPMENT_INFORMATION_DAY:
                return new EventDate(Month.OCTOBER, 24, year);
            case WORLD_AIDS_DAY: return new EventDate(Month.DECEMBER, 1, year);
            case WORLD_AUTISM_AWARENESS_DAY: return new EventDate(Month.APRIL, 2, year);
            case WORLD_BEE_DAY: return new EventDate(Month.MAY, 20, year);
            case WORLD_BICYCLE_DAY: return new EventDate(Month.JUNE, 3, year);
            case WORLD_BLOOD_DONOR_DAY: return new EventDate(Month.JUNE, 14, year);
            case WORLD_BRAILLE_DAY: return new EventDate(Month.JANUARY, 4, year);
            case WORLD_CANCER_DAY: return new EventDate(Month.FEBRUARY, 4, year);
            case WORLD_CITIES_DAY: return new EventDate(Month.OCTOBER, 31, year);
            case WORLD_CREATIVITY_AND_INNOVATION_DAY: return new EventDate(Month.APRIL, 21, year);
            case WORLD_ENVIRONMENT_DAY: return new EventDate(Month.JUNE, 5, year);
            case WORLD_FOOD_DAY: return new EventDate(Month.OCTOBER, 16, year);
            case WORLD_HABITAT_DAY: return getFirst(DayOfWeek.MONDAY, Month.OCTOBER, year);
            case WORLD_HEALTH_DAY: return new EventDate(Month.APRIL, 7, year);
            case WORLD_HEPATITIS_DAY: return new EventDate(Month.JULY, 28, year);
            case WORLD_HUMANITARIAN_DAY: return new EventDate(Month.AUGUST, 19, year);
            case WORLD_LOGIC_DAY: return new EventDate(Month.JANUARY, 14, year);
            case WORLD_MALARIA_DAY: return new EventDate(Month.APRIL, 25, year);
            case WORLD_NO_TOBACCO_DAY: return new EventDate(Month.MAY, 31, year);
            case WORLD_PHILOSOPHY_DAY: return getThird(DayOfWeek.THURSDAY, Month.NOVEMBER, year);
            case WORLD_POETRY_DAY: return new EventDate(Month.MARCH, 21, year);
            case WORLD_PRESS_FREEDOM_DAY: return new EventDate(Month.MAY, 3, year);
            case WORLD_RADIO_DAY: return new EventDate(Month.FEBRUARY, 13, year);
            case WORLD_TOILET_DAY: return new EventDate(Month.NOVEMBER, 19, year);
            case WORLD_TOURISM_DAY: return new EventDate(Month.SEPTEMBER, 27, year);
            case WORLD_WATER_DAY: return new EventDate(Month.MARCH, 22, year);
            case WORLD_WILDLIFE_DAY: return new EventDate(Month.MARCH, 3, year);
            case ZERO_DISCRIMINATION_DAY: return new EventDate(Month.MARCH, 1, year);
        }
        return null;
    }

    @Override
    public EventSources getSources(WLCountry country) {
        switch (this) {
            case INTERNATIONAL_DAY_OF_LIGHT:
                return new EventSources(
                        new EventSource("Official Website", "https://www.lightday.org")
                );
            default:
                return getDefaultSources();
        }
    }
}
