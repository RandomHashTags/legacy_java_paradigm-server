package me.randomhashtags.worldlaws.observances.type.unfinished;

import me.randomhashtags.worldlaws.EventDate;
import me.randomhashtags.worldlaws.country.WLCountry;
import me.randomhashtags.worldlaws.observances.IHoliday;

import java.time.DayOfWeek;
import java.time.Month;

public enum WeeklyHoliday implements IHoliday {

    CONSTITUTION_WEEK,
    EARTH_SCIENCE_WEEK,
    FIJI_WEEK,
    INTERNATIONAL_CLOWN_WEEK,

    NATIONAL_CHEMISTRY_WEEK,
    NATIONAL_SUICIDE_PREVENTION_WEEK,
    SUNSHINE_WEEK,

    ;

    private final String wikipediaName;

    WeeklyHoliday() {
        this(null);
    }
    WeeklyHoliday(String wikipediaName) {
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
        return null;
    }

    @Override
    public EventDate getDate(WLCountry country, int year) {
        switch (this) {
            case CONSTITUTION_WEEK:
                switch (country) {
                    case UNITED_STATES: return new EventDate(Month.SEPTEMBER, 17, year);
                    default: return null;
                }
            case EARTH_SCIENCE_WEEK:
                switch (country) {
                    case UNITED_STATES: return null;
                    default: return null;
                }
            case FIJI_WEEK:
                switch (country) {
                    case FIJI: return null;
                    default: return null;
                }
            case INTERNATIONAL_CLOWN_WEEK: return new EventDate(Month.AUGUST, 1, year);
            case NATIONAL_CHEMISTRY_WEEK:
                switch (country) {
                    case UNITED_STATES: return getThird(DayOfWeek.SUNDAY, Month.OCTOBER, year);
                    default: return null;
                }
            case NATIONAL_SUICIDE_PREVENTION_WEEK:
                switch (country) {
                    case UNITED_STATES: return null;
                    default: return null;
                }
            case SUNSHINE_WEEK:
                switch (country) {
                    case UNITED_STATES: return null;
                    default: return null;
                }
            default:
                return null;
        }
    }
}
