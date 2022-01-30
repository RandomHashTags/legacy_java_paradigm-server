package me.randomhashtags.worldlaws.observances.type.unfinished;

import me.randomhashtags.worldlaws.EventDate;
import me.randomhashtags.worldlaws.country.WLCountry;
import me.randomhashtags.worldlaws.observances.IHoliday;

import java.time.Month;

public enum MonthlyHoliday implements IHoliday {

    ACADEMIC_WRITING_MONTH,
    ASIAN_PACIFIC_AMERICAN_HERITAGE_MONTH,

    BLACK_HISTORY_MONTH,

    COPD_AWARENESS_MONTH,

    FILIPINO_AMERICAN_HISTORY_MONTH,

    MENTAL_HEALTH_AWARENESS_MONTH,

    NATIONAL_BIKE_MONTH,
    NATIONAL_BIRD_FEEDING_MONTH(
            "National Bird-feeding Month"
    ),
    NATIONAL_CHILDHOOD_CANCER_AWARENESS_MONTH,
    NATIONAL_COLON_CANCER_AWARENESS_MONTH,
    NATIONAL_CYBER_SECURITY_AWARENESS_MONTH,
    NATIONAL_DISABILITY_EMPLOYMENT_AWARENESS_MONTH,
    NATIONAL_FRUITS_AND_VEGGIES_MONTH(
            "National Fruits & Veggies Month"
    ),
    NATIONAL_HONEY_MONTH,
    NATIONAL_ICE_CREAM_MONTH,
    NATIONAL_MENTORING_MONTH,
    NATIONAL_NOVEL_WRITING_MONTH,
    NATIONAL_PET_MONTH,
    NATIONAL_POETRY_WRITING_MONTH,
    NATIONAL_PREPAREDNESS_MONTH,
    NATIONAL_PROSTATE_HEALTH_MONTH,
    NATIONAL_SAFETY_MONTH,
    NATIONAL_STROKE_AWARENESS_MONTH,
    NATIONAL_VOLUNTEER_MONTH,

    PAIN_AWARENESS_MONTH,

    SECOND_CHANCE_MONTH,
    SEXUAL_ASSAULT_AWARENESS_MONTH,

    WOMENS_HISTORY_MONTH(
            "Women's History Month"
    ),
    ;

    private final String wikipediaName;

    MonthlyHoliday() {
        this(null);
    }
    MonthlyHoliday(String wikipediaName) {
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
            case COPD_AWARENESS_MONTH: return collectAliases("National Chronic Obstructive Pulmonary Disease Awareness Month");
            case BLACK_HISTORY_MONTH: return collectAliases("African-American History Month");
            default: return null;
        }
    }

    @Override
    public EventDate getDate(WLCountry country, int year) {
        switch (this) {
            case ACADEMIC_WRITING_MONTH:
            case COPD_AWARENESS_MONTH:
            case NATIONAL_NOVEL_WRITING_MONTH:
                switch (country) {
                    case UNITED_STATES: return new EventDate(Month.NOVEMBER, 0, year);
                    default: return null;
                }
            case ASIAN_PACIFIC_AMERICAN_HERITAGE_MONTH:
            case MENTAL_HEALTH_AWARENESS_MONTH:
            case NATIONAL_BIKE_MONTH:
                switch (country) {
                    case UNITED_STATES: return new EventDate(Month.MAY, 0, year);
                    default: return null;
                }
            case BLACK_HISTORY_MONTH:
                switch (country) {
                    case CANADA:
                    case UNITED_STATES:
                        return new EventDate(Month.FEBRUARY, 0, year);
                    case IRELAND:
                    case NETHERLANDS:
                    case UNITED_KINGDOM:
                        return new EventDate(Month.OCTOBER, 0, year);
                    default:
                        return null;
                }
            case FILIPINO_AMERICAN_HISTORY_MONTH:
            case NATIONAL_CHILDHOOD_CANCER_AWARENESS_MONTH:
            case NATIONAL_CYBER_SECURITY_AWARENESS_MONTH:
            case NATIONAL_DISABILITY_EMPLOYMENT_AWARENESS_MONTH:
                switch (country) {
                    case UNITED_STATES: return new EventDate(Month.OCTOBER, 0, year);
                    default: return null;
                }
            case NATIONAL_BIRD_FEEDING_MONTH:
                switch (country) {
                    case UNITED_STATES: return new EventDate(Month.FEBRUARY, 0, year);
                    default: return null;
                }
            case NATIONAL_COLON_CANCER_AWARENESS_MONTH:
                switch (country) {
                    case UNITED_STATES: return new EventDate(Month.MARCH, 0, year);
                    default: return null;
                }
            case NATIONAL_FRUITS_AND_VEGGIES_MONTH:
            case NATIONAL_HONEY_MONTH:
            case NATIONAL_PREPAREDNESS_MONTH:
            case NATIONAL_PROSTATE_HEALTH_MONTH:
            case PAIN_AWARENESS_MONTH:
                switch (country) {
                    case UNITED_STATES: return new EventDate(Month.SEPTEMBER, 0, year);
                    default: return null;
                }
            case NATIONAL_ICE_CREAM_MONTH:
                switch (country) {
                    case UNITED_STATES: return new EventDate(Month.JULY, 0, year);
                    default: return null;
                }
            case NATIONAL_MENTORING_MONTH:
                switch (country) {
                    case UNITED_STATES: return new EventDate(Month.JANUARY, 0, year);
                    default: return null;
                }
            case NATIONAL_POETRY_WRITING_MONTH:
            case NATIONAL_STROKE_AWARENESS_MONTH:
            case NATIONAL_VOLUNTEER_MONTH:
            case SECOND_CHANCE_MONTH:
            case SEXUAL_ASSAULT_AWARENESS_MONTH:
                switch (country) {
                    case UNITED_STATES: return new EventDate(Month.APRIL, 0, year);
                    default: return null;
                }
            case NATIONAL_PET_MONTH:
                switch (country) {
                    case UNITED_KINGDOM: return new EventDate(Month.APRIL, 0, year);
                    case UNITED_STATES: return new EventDate(Month.MAY, 0, year);
                    default: return null;
                }
            case NATIONAL_SAFETY_MONTH:
                switch (country) {
                    case UNITED_STATES: return new EventDate(Month.JUNE, 0, year);
                    default: return null;
                }
            case WOMENS_HISTORY_MONTH:
                switch (country) {
                    case AUSTRALIA:
                    case UNITED_KINGDOM:
                    case UNITED_STATES:
                        return new EventDate(Month.MARCH, 0, year);
                    case CANADA:
                        return new EventDate(Month.OCTOBER, 0, year);
                    default:
                        return null;
                }
            default:
                return null;
        }
    }
}
