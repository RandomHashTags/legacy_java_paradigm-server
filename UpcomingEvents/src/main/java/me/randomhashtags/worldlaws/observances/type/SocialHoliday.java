package me.randomhashtags.worldlaws.observances.type;

import me.randomhashtags.worldlaws.EventDate;
import me.randomhashtags.worldlaws.location.WLCountry;
import me.randomhashtags.worldlaws.observances.IHoliday;
import me.randomhashtags.worldlaws.observances.custom.*;

import java.time.DayOfWeek;
import java.time.Month;

public enum SocialHoliday implements IHoliday {

    AFRICA_DAY,
    ARMED_FORCES_DAY,
    BLACK_AWARENESS_DAY,
    BLACK_FRIDAY(
            "Black Friday (shopping)"
    ),
    BOXING_DAY,
    CONSTITUTION_DAY,
    CYBER_MONDAY,
    DAY_OF_VALOR,
    FARMERS_DAY(
            "Farmers' Day"
    ),
    FATHERS_DAY(
            "Father's Day"
    ),

    GERMAN_UNITY_DAY,
    GROUNDHOG_DAY,

    HEROES_DAY(
            "Heroes' Day"
    ),

    INTERNATIONAL_WORKERS_DAY(
            "International Workers' Day"
    ),

    KOREAN_ALPHABET_DAY(
            "Hangul Day"
    ),
    LABOUR_DAY,
    LABOUR_SAFETY_DAY(
            "Labour Day"
    ),
    MAY_DAY,

    NATIONAL_DAY_OF_SPAIN,

    ORANGE_SHIRT_DAY,

    LIBERATION_DAY,

    MARTYRS_DAY(
            "Martyr's Day"
    ),

    MOTHERHOOD_AND_BEAUTY_DAY,
    MOTHERS_DAY(
            "Mother's Day" // https://en.wikipedia.org/wiki/Mother%27s_Day
    ),

    NATIONAL_CANCER_SURVIVORS_DAY,
    NATIONAL_SCIENCE_DAY,

    REPUBLIC_DAY,

    TEACHERS_DAY(
            "Teacher's Day"
    ),
    THANKSGIVING,

    VETERANS_DAY,
    VICTORY_DAY,

    ;

    private final String wikipediaName;

    SocialHoliday() {
        this(null);
    }
    SocialHoliday(String wikipediaName) {
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
            case BLACK_AWARENESS_DAY: return new String[] { "Black Consciousness Day", "Zumbi Day" };
            case BOXING_DAY: return new String[] { "Offering Day" };
            case DAY_OF_VALOR: return new String[] { "Bataan Day", "Bataan and Corregidor Day" };
            case HEROES_DAY: return new String[] { "National Heroes' Day" };
            case INTERNATIONAL_WORKERS_DAY: return new String[] { "Labour Day", "May Day" };
            case KOREAN_ALPHABET_DAY: return new String[] { "Hangul Day", "Chosŏn'gŭl Day" };
            case VETERANS_DAY: return new String[] { "Armistice Day" };
            default: return null;
        }
    }

    @Override
    public EventDate getDate(WLCountry country, int year) {
        switch (this) {
            case AFRICA_DAY: return AfricaDay.INSTANCE.getDate(country, year);
            case ARMED_FORCES_DAY: return ArmedForcesDay.INSTANCE.getDate(country, year);
            case BLACK_AWARENESS_DAY:
                switch (country) {
                    case BRAZIL: return new EventDate(Month.NOVEMBER, 20, year);
                    default: return null;
                }
            case BLACK_FRIDAY:
                final EventDate thanksgiving = THANKSGIVING.getDate(WLCountry.UNITED_STATES, year);
                return thanksgiving != null ? thanksgiving.plusDays(1) : null;
            case BOXING_DAY: return BoxingDay.INSTANCE.getDate(country, year);
            case CONSTITUTION_DAY: return ConstitutionDay.INSTANCE.getDate(country, year);
            case CYBER_MONDAY:
                final EventDate blackFriday = BLACK_FRIDAY.getDate(country, year);
                return blackFriday != null ? blackFriday.plusDays(3) : null;
            case DAY_OF_VALOR: return DayOfValor.INSTANCE.getDate(country, year);
            case FARMERS_DAY: return FarmersDay.INSTANCE.getDate(country, year);
            case FATHERS_DAY: return FathersDay.INSTANCE.getDate(country, year);
            case GERMAN_UNITY_DAY:
                switch (country) {
                    case GERMANY: return new EventDate(Month.OCTOBER, 3, year);
                    default: return null;
                }
            case GROUNDHOG_DAY:
                switch (country) {
                    case CANADA:
                    case UNITED_STATES:
                        return new EventDate(Month.FEBRUARY, 2, year);
                    default:
                        return null;
                }
            case HEROES_DAY: return HeroesDay.INSTANCE.getDate(country, year);

            case INTERNATIONAL_WORKERS_DAY:
                switch (country) {
                    case GERMANY:
                    case SWEDEN:
                        return new EventDate(Month.MAY, 1, year);
                    default: return null;
                }
            case ORANGE_SHIRT_DAY:
                switch (country) {
                    case CANADA: return new EventDate(Month.SEPTEMBER, 30, year);
                    default: return null;
                }
            case KOREAN_ALPHABET_DAY:
                switch (country) {
                    case NORTH_KOREA: return new EventDate(Month.JANUARY, 15, year);
                    case SOUTH_KOREA: return new EventDate(Month.OCTOBER, 9, year);
                    default: return null;
                }
            case LABOUR_DAY: return LabourDay.INSTANCE.getDate(country, year);
            case LABOUR_SAFETY_DAY:
                switch (country) {
                    case BANGLADESH: return new EventDate(Month.APRIL, 24, year);
                    default: return null;
                }
            case LIBERATION_DAY: return LiberationDay.INSTANCE.getDate(country, year);
            case MARTYRS_DAY: return MartyrsDay.INSTANCE.getDate(country, year);
            case MAY_DAY:
                switch (country) {
                    case BULGARIA:
                    case CANADA:
                    case CZECH_REPUBLIC:
                    case ESTONIA:
                    case FINLAND:
                    case FRANCE:
                    case GERMANY:
                    case GREECE:
                    case IRELAND:
                    case ITALY:
                    case SCOTLAND:
                        return new EventDate(Month.MAY, 1, year);
                    default: return null;
                }
            case MOTHERHOOD_AND_BEAUTY_DAY:
                switch (country) {
                    case ARMENIA: return new EventDate(Month.APRIL, 7, year);
                    default: return null;
                }
            case MOTHERS_DAY: return MothersDay.INSTANCE.getDate(country, year);
            case NATIONAL_CANCER_SURVIVORS_DAY:
                switch (country) {
                    case ANTIGUA_AND_BARBUDA:
                    case UNITED_STATES:
                        return getFirst(DayOfWeek.SUNDAY, Month.JUNE, year);
                    default:
                        return null;
                }
            case NATIONAL_DAY_OF_SPAIN:
                switch (country) {
                    case SPAIN: return new EventDate(Month.OCTOBER, 12, year);
                    default: return null;
                }
            case NATIONAL_SCIENCE_DAY: return NationalScienceDay.INSTANCE.getDate(country, year);
            case REPUBLIC_DAY: return RepublicDay.INSTANCE.getDate(country, year);
            case TEACHERS_DAY: return TeachersDay.INSTANCE.getDate(country, year);
            case THANKSGIVING: return Thanksgiving.INSTANCE.getDate(country, year);
            case VETERANS_DAY: return VeteransDay.INSTANCE.getDate(country, year);
            case VICTORY_DAY: return VictoryDay.INSTANCE.getDate(country, year);
            default:
                return null;
        }
    }
}
