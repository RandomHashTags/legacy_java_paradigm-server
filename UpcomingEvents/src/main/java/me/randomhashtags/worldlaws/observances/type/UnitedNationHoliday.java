package me.randomhashtags.worldlaws.observances.type;

import me.randomhashtags.worldlaws.EventDate;
import me.randomhashtags.worldlaws.EventSource;
import me.randomhashtags.worldlaws.EventSources;
import me.randomhashtags.worldlaws.country.WLCountry;
import me.randomhashtags.worldlaws.observances.Holiday;
import me.randomhashtags.worldlaws.observances.HolidaySource;
import me.randomhashtags.worldlaws.observances.HolidayType;

import java.time.DayOfWeek;
import java.time.Month;

public enum UnitedNationHoliday implements Holiday {
    // https://en.wikipedia.org/wiki/Category:United_Nations_days

    ASTEROID_DAY,

    EARTH_DAY,

    GLOBAL_DAY_OF_PARENTS(
            "Parent's Day"
    ),
    GLOBAL_HANDWASHING_DAY,

    HUMAN_RIGHTS_DAY,

    INTERNATIONAL_ALBINISM_AWARENESS_DAY,
    INTERNATIONAL_ANTI_CORRUPTION_DAY(
            "International Anti-Corruption Day"
    ),
    INTERNATIONAL_CHERNOBYL_DISASTER_REMEMBRANCE_DAY,
    INTERNATIONAL_COOPERATIVE_DAY(
            "International Co-operative Day"
    ),
    INTERNATIONAL_DANCE_DAY,
    INTERNATIONAL_DAY_FOR_THE_ABOLITION_OF_SLAVERY,
    INTERNATIONAL_DAY_FOR_THE_PRESERVATION_OF_THE_OZONE_LAYER,
    INTERNATIONAL_DAY_OF_DEMOCRACY,
    INTERNATIONAL_DAY_OF_EDUCATION,
    INTERNATIONAL_DAY_OF_FAMILIES,
    INTERNATIONAL_DAY_OF_FORESTS,
    INTERNATIONAL_DAY_OF_HAPPINESS,
    INTERNATIONAL_DAY_OF_HUMAN_SPACE_FLIGHT,
    INTERNATIONAL_DAY_OF_LIGHT,
    INTERNATIONAL_DAY_OF_PEACE,
    INTERNATIONAL_DAY_OF_THE_WORLDS_INDIGENOUS_PEOPLES(
            "International Day of the World's Indigenous Peoples"
    ),
    INTERNATIONAL_DAY_OF_WOMEN_AND_GIRLS_IN_SCIENCE,
    INTERNATIONAL_DAY_OF_YOGA,
    INTERNATIONAL_FIREFIGHTERS_DAY(
            "International Firefighters' Day"
    ),
    INTERNATIONAL_HOLOCAUST_REMEMBRANCE_DAY,
    INTERNATIONAL_HUMAN_SOLIDARITY_DAY,
    INTERNATIONAL_LITERACY_DAY,
    INTERNATIONAL_MOTHER_EARTH_DAY,
    INTERNATIONAL_NURSES_DAY,
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
    WORLD_DAY_AGAINST_CHILD_LABOUR,
    WORLD_DAY_OF_REMEMBRANCE_FOR_ROAD_TRAFFIC_VICTIMS,
    WORLD_DAY_TO_COMBAT_DESERTIFICATION_AND_DROUGHT,
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
    WORLD_METEOROLOGICAL_DAY,
    WORLD_MILK_DAY,
    WORLD_NO_TOBACCO_DAY,
    WORLD_OCEANS_DAY,
    WORLD_PATIENT_SAFETY_DAY,
    WORLD_PHILOSOPHY_DAY,
    WORLD_POETRY_DAY,
    WORLD_POPULATION_DAY,
    WORLD_PRESS_FREEDOM_DAY,
    WORLD_RADIO_DAY,
    WORLD_REFUGEE_DAY,
    WORLD_STUDENTS_DAY(
            "World Students' Day"
    ),
    WORLD_TELECOMMUNICATION_AND_INFORMATION_SOCIETY_DAY,
    WORLD_TELEVISION_DAY,
    WORLD_TOILET_DAY,
    WORLD_TOURISM_DAY,
    WORLD_TUBERCULOSIS_DAY,
    WORLD_WATER_DAY,
    WORLD_WILDLIFE_DAY,

    ZERO_DISCRIMINATION_DAY,

    ARABIC_LANGUAGE_DAY,
    CHINESE_LANGUAGE_DAY,
    ENGLISH_LANGUAGE_DAY,
    SPANISH_LANGUAGE_DAY,
    FRENCH_LANGUAGE_DAY,
    RUSSIAN_LANGUAGE_DAY,
    ;

    private final String wikipediaName;

    UnitedNationHoliday() {
        this(null);
    }
    UnitedNationHoliday(String wikipediaName) {
        this.wikipediaName = wikipediaName;
    }

    @Override
    public HolidayType getType() {
        return HolidayType.UNITED_NATIONS;
    }

    @Override
    public String getWikipediaName() {
        return wikipediaName;
    }

    @Override
    public String[] getAliases() {
        switch (this) {
            case ASTEROID_DAY: return collectAliases("International Asteroid Day");
            case INTERNATIONAL_ALBINISM_AWARENESS_DAY: return collectAliases("IAAD");
            case INTERNATIONAL_DAY_OF_LIGHT: return collectAliases("Light Day");
            default: return null;
        }
    }

    @Override
    public EventDate getDate(WLCountry country, int year) {
        if(country == null || !country.isUNMemberState() && !country.isUNObserverState()) {
            return null;
        }
        switch (this) {
            case ASTEROID_DAY: return new EventDate(Month.JUNE, 30, year);
            case EARTH_DAY:
            case INTERNATIONAL_MOTHER_EARTH_DAY:
                return new EventDate(Month.APRIL, 22, year);
            case GLOBAL_DAY_OF_PARENTS: return new EventDate(Month.JUNE, 1, year);
            case GLOBAL_HANDWASHING_DAY: return new EventDate(Month.OCTOBER, 15, year);
            case HUMAN_RIGHTS_DAY: return new EventDate(Month.DECEMBER, 10, year);
            case INTERNATIONAL_ALBINISM_AWARENESS_DAY: return new EventDate(Month.JUNE, 13, year);
            case INTERNATIONAL_ANTI_CORRUPTION_DAY: return new EventDate(Month.DECEMBER, 9, year);
            case INTERNATIONAL_CHERNOBYL_DISASTER_REMEMBRANCE_DAY: return new EventDate(Month.APRIL, 26, year);
            case INTERNATIONAL_COOPERATIVE_DAY: return EventDate.getFirst(DayOfWeek.SATURDAY, Month.JULY, year);
            case INTERNATIONAL_DANCE_DAY: return new EventDate(Month.APRIL, 29, year);
            case INTERNATIONAL_DAY_FOR_THE_ABOLITION_OF_SLAVERY: return new EventDate(Month.DECEMBER, 2, year);
            case INTERNATIONAL_DAY_FOR_THE_PRESERVATION_OF_THE_OZONE_LAYER: return new EventDate(Month.SEPTEMBER, 16, year);
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
            case INTERNATIONAL_DAY_OF_THE_WORLDS_INDIGENOUS_PEOPLES: return new EventDate(Month.AUGUST, 9, year);
            case INTERNATIONAL_DAY_OF_WOMEN_AND_GIRLS_IN_SCIENCE: return new EventDate(Month.FEBRUARY, 11, year);
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
            case NATIONAL_CLEANUP_DAY: return EventDate.getThird(DayOfWeek.SATURDAY, Month.SEPTEMBER, year);
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
            case WORLD_DAY_AGAINST_CHILD_LABOUR: return new EventDate(Month.JUNE, 12, year);
            case WORLD_DAY_OF_REMEMBRANCE_FOR_ROAD_TRAFFIC_VICTIMS: return EventDate.getThird(DayOfWeek.SUNDAY, Month.NOVEMBER, year);
            case WORLD_DAY_TO_COMBAT_DESERTIFICATION_AND_DROUGHT: return new EventDate(Month.JUNE, 17, year);
            case WORLD_ENVIRONMENT_DAY: return new EventDate(Month.JUNE, 5, year);
            case WORLD_FOOD_DAY: return new EventDate(Month.OCTOBER, 16, year);
            case WORLD_HABITAT_DAY: return EventDate.getFirst(DayOfWeek.MONDAY, Month.OCTOBER, year);
            case WORLD_HEALTH_DAY: return new EventDate(Month.APRIL, 7, year);
            case WORLD_HEPATITIS_DAY: return new EventDate(Month.JULY, 28, year);
            case WORLD_HUMANITARIAN_DAY: return new EventDate(Month.AUGUST, 19, year);
            case WORLD_LOGIC_DAY: return new EventDate(Month.JANUARY, 14, year);
            case WORLD_MALARIA_DAY: return new EventDate(Month.APRIL, 25, year);
            case WORLD_METEOROLOGICAL_DAY: return new EventDate(Month.MARCH, 23, year);
            case WORLD_MILK_DAY: return new EventDate(Month.JUNE, 1, year);
            case WORLD_NO_TOBACCO_DAY: return new EventDate(Month.MAY, 31, year);
            case WORLD_OCEANS_DAY: return new EventDate(Month.JUNE, 8, year);
            case WORLD_PATIENT_SAFETY_DAY: return new EventDate(Month.SEPTEMBER, 17, year);
            case WORLD_PHILOSOPHY_DAY: return EventDate.getThird(DayOfWeek.THURSDAY, Month.NOVEMBER, year);
            case WORLD_POETRY_DAY: return new EventDate(Month.MARCH, 21, year);
            case WORLD_POPULATION_DAY: return new EventDate(Month.JULY, 22, year);
            case WORLD_PRESS_FREEDOM_DAY: return new EventDate(Month.MAY, 3, year);
            case WORLD_RADIO_DAY: return new EventDate(Month.FEBRUARY, 13, year);
            case WORLD_REFUGEE_DAY: return new EventDate(Month.JUNE, 20, year);
            case WORLD_STUDENTS_DAY: return new EventDate(Month.OCTOBER, 15, year);
            case WORLD_TELECOMMUNICATION_AND_INFORMATION_SOCIETY_DAY: return new EventDate(Month.MAY, 17, year);
            case WORLD_TELEVISION_DAY: return new EventDate(Month.NOVEMBER, 21, year);
            case WORLD_TOILET_DAY: return new EventDate(Month.NOVEMBER, 19, year);
            case WORLD_TOURISM_DAY: return new EventDate(Month.SEPTEMBER, 27, year);
            case WORLD_TUBERCULOSIS_DAY: return new EventDate(Month.MARCH, 24, year);
            case WORLD_WATER_DAY: return new EventDate(Month.MARCH, 22, year);
            case WORLD_WILDLIFE_DAY: return new EventDate(Month.MARCH, 3, year);
            case ZERO_DISCRIMINATION_DAY: return new EventDate(Month.MARCH, 1, year);

            case ARABIC_LANGUAGE_DAY: return new EventDate(Month.DECEMBER, 18, year);
            case CHINESE_LANGUAGE_DAY: return new EventDate(Month.APRIL, 20, year);
            case ENGLISH_LANGUAGE_DAY: return new EventDate(Month.APRIL, 23, year);
            case SPANISH_LANGUAGE_DAY: return new EventDate(Month.APRIL, 23, year);
            case FRENCH_LANGUAGE_DAY: return new EventDate(Month.MARCH, 20, year);
            case RUSSIAN_LANGUAGE_DAY: return new EventDate(Month.JUNE, 6, year);
            default: return null;
        }
    }

    @Override
    public HolidaySource getSource() {
        switch (this) {
            case ARABIC_LANGUAGE_DAY:
            case CHINESE_LANGUAGE_DAY:
            case ENGLISH_LANGUAGE_DAY:
            case SPANISH_LANGUAGE_DAY:
            case FRENCH_LANGUAGE_DAY:
            case RUSSIAN_LANGUAGE_DAY:
                return HolidaySource.UNITED_NATIONS;
            default:
                return HolidaySource.WIKIPEDIA;
        }
    }

    @Override
    public EventSources getSources(WLCountry country) {
        final String unitedNations = "United Nations";
        switch (this) {
            case INTERNATIONAL_DAY_OF_LIGHT:
                return collectSources(
                        new EventSource("Official Website", "https://www.lightday.org")
                );
            case ARABIC_LANGUAGE_DAY:
                return collectSources(
                        new EventSource(unitedNations, "https://www.un.org/ar/observances/arabiclanguageday/")
                );
            case CHINESE_LANGUAGE_DAY:
                return collectSources(
                        new EventSource(unitedNations, "https://www.un.org/zh/observances/chinese-language-day")
                );
            case ENGLISH_LANGUAGE_DAY:
                return collectSources(
                        new EventSource(unitedNations, "https://www.un.org/en/observances/english-language-day")
                );
            case SPANISH_LANGUAGE_DAY:
                return collectSources(
                        new EventSource(unitedNations, "https://www.un.org/es/observances/spanish-language-day")
                );
            case FRENCH_LANGUAGE_DAY:
                return collectSources(
                        new EventSource(unitedNations, "https://www.un.org/fr/observances/french-language-day/")
                );
            case RUSSIAN_LANGUAGE_DAY:
                return collectSources(
                        new EventSource(unitedNations, "https://www.un.org/ru/observances/russian-language-day")
                );

            default:
                return getDefaultSources();
        }
    }
}
