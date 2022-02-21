package me.randomhashtags.worldlaws.observances.type;

import me.randomhashtags.worldlaws.EventDate;
import me.randomhashtags.worldlaws.EventSources;
import me.randomhashtags.worldlaws.country.WLCountry;
import me.randomhashtags.worldlaws.observances.IHoliday;
import me.randomhashtags.worldlaws.observances.custom.*;

import java.time.DayOfWeek;
import java.time.Month;

public enum SocialHoliday implements IHoliday {

    AFRICA_DAY,
    ANZAC_DAY,
    ARMED_FORCES_DAY,
    BLACK_AWARENESS_DAY,
    BLACK_FRIDAY(
            "Black Friday (shopping)"
    ),
    BOXING_DAY,
    CONSTITUTION_DAY,
    CYBER_MONDAY,
    DATA_PRIVACY_DAY,
    DAY_OF_VALOR,
    EMANCIPATION_DAY,
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
    MATARIKI,

    MOTHERHOOD_AND_BEAUTY_DAY,
    MOTHERS_DAY(
            "Mother's Day" // https://en.wikipedia.org/wiki/Mother%27s_Day
    ),

    NATIONAL_CANCER_SURVIVORS_DAY,
    NATIONAL_SCIENCE_DAY,

    QUEENS_BIRTHDAY(
            "Queen's Official Birthday"
    ),

    REPUBLIC_DAY,

    TEACHERS_DAY(
            "Teacher's Day"
    ),
    THANKSGIVING,

    VETERANS_DAY,
    VICTORY_DAY,

    WAITANGI_DAY,
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
    public String getOfficialName() {
        final IHoliday holiday = getHoliday();
        if(holiday != null) {
            final String name = holiday.getOfficialName();
            if(name != null) {
                return name;
            }
        }
        return wikipediaName;
    }

    @Override
    public String[] getAliases() {
        final IHoliday holiday = getHoliday();
        if(holiday != null) {
            return holiday.getAliases();
        }
        switch (this) {
            case BLACK_AWARENESS_DAY: return collectAliases("Black Consciousness Day", "Zumbi Day");
            case KOREAN_ALPHABET_DAY: return collectAliases("Hangul Day", "Chosŏn'gŭl Day");
            default: return null;
        }
    }

    @Override
    public EventDate getDate(WLCountry country, int year) {
        final IHoliday holiday = getHoliday();
        if(holiday != null) {
            return holiday.getDate(country, year);
        }
        switch (this) {
            case BLACK_AWARENESS_DAY:
                switch (country) {
                    case BRAZIL: return new EventDate(Month.NOVEMBER, 20, year);
                    default: return null;
                }
            case BLACK_FRIDAY:
                final EventDate thanksgiving = THANKSGIVING.getDate(WLCountry.UNITED_STATES, year);
                return thanksgiving != null ? thanksgiving.plusDays(1) : null;
            case CYBER_MONDAY:
                final EventDate blackFriday = BLACK_FRIDAY.getDate(country, year);
                return blackFriday != null ? blackFriday.plusDays(3) : null;
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
            case LABOUR_SAFETY_DAY:
                switch (country) {
                    case BANGLADESH: return new EventDate(Month.APRIL, 24, year);
                    default: return null;
                }
            case MATARIKI:
                switch (country) {
                    case NEW_ZEALAND: return null; // TODO: calculate this day!! (https://en.wikipedia.org/wiki/Matariki)
                    default: return null;
                }
            case MOTHERHOOD_AND_BEAUTY_DAY:
                switch (country) {
                    case ARMENIA: return new EventDate(Month.APRIL, 7, year);
                    default: return null;
                }
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
            case WAITANGI_DAY:
                switch (country) {
                    case NEW_ZEALAND: return new EventDate(Month.FEBRUARY, 6, year);
                    default: return null;
                }
            default:
                return null;
        }
    }

    @Override
    public EventSources getSources(WLCountry country) {
        final IHoliday holiday = getHoliday();
        return holiday != null ? holiday.getSources(country) : getDefaultSources();
    }

    private IHoliday getHoliday() {
        switch (this) {
            case AFRICA_DAY: return AfricaDay.INSTANCE;
            case ANZAC_DAY: return AnzacDay.INSTANCE;
            case ARMED_FORCES_DAY: return ArmedForcesDay.INSTANCE;
            case BOXING_DAY: return BoxingDay.INSTANCE;
            case CONSTITUTION_DAY: return ConstitutionDay.INSTANCE;
            case DATA_PRIVACY_DAY: return DataPrivacyDay.INSTANCE;
            case DAY_OF_VALOR: return DayOfValor.INSTANCE;
            case EMANCIPATION_DAY: return EmancipationDay.INSTANCE;
            case FARMERS_DAY: return FarmersDay.INSTANCE;
            case FATHERS_DAY: return FathersDay.INSTANCE;
            case HEROES_DAY: return HeroesDay.INSTANCE;
            case INTERNATIONAL_WORKERS_DAY: return InternationalWorkersDay.INSTANCE;
            case LABOUR_DAY: return LabourDay.INSTANCE;
            case LIBERATION_DAY: return LiberationDay.INSTANCE;
            case MARTYRS_DAY: return MartyrsDay.INSTANCE;
            case MAY_DAY: return MayDay.INSTANCE;
            case MOTHERS_DAY: return MothersDay.INSTANCE;
            case NATIONAL_SCIENCE_DAY: return NationalScienceDay.INSTANCE;
            case QUEENS_BIRTHDAY: return QueensBirthday.INSTANCE;
            case REPUBLIC_DAY: return RepublicDay.INSTANCE;
            case TEACHERS_DAY: return TeachersDay.INSTANCE;
            case THANKSGIVING: return Thanksgiving.INSTANCE;
            case VETERANS_DAY: return VeteransDay.INSTANCE;
            case VICTORY_DAY: return VictoryDay.INSTANCE;
            default: return null;
        }
    }
}
