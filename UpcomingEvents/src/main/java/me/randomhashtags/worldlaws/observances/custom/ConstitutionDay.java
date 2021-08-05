package me.randomhashtags.worldlaws.observances.custom;

import me.randomhashtags.worldlaws.EventDate;
import me.randomhashtags.worldlaws.country.WLCountry;
import me.randomhashtags.worldlaws.observances.IHoliday;

import java.time.DayOfWeek;
import java.time.Month;

public enum ConstitutionDay implements IHoliday { // https://en.wikipedia.org/wiki/Constitution_Day
    INSTANCE;

    @Override
    public Enum<? extends IHoliday> getEnum() {
        return null;
    }

    @Override
    public String getOfficialName() {
        return null;
    }

    @Override
    public String[] getAliases() {
        return new String[] { "Citizenship Day" };
    }

    public EventDate getDate(WLCountry country, int year) {
        switch (country) {
            case ABKHAZIA: return new EventDate(Month.NOVEMBER, 26, year);
            case ANDORRA: return new EventDate(Month.MARCH, 14, year);
            case ARGENTINA:
            case LATVIA:
            case MARSHALL_ISLANDS:
                return new EventDate(Month.MAY, 1, year);
            case ARMENIA: return new EventDate(Month.JULY, 5, year);
            case AUSTRALIA:
            case PALAU:
                return new EventDate(Month.JULY, 9, year);
            case AZERBAIJAN: return new EventDate(Month.NOVEMBER, 12, year);
            case BELARUS: return new EventDate(Month.MARCH, 15, year);
            case BELGIUM: return new EventDate(Month.JULY, 21, year);
            case BRAZIL: return new EventDate(Month.NOVEMBER, 15, year);
            case CAMBODIA: return new EventDate(Month.SEPTEMBER, 24, year);
            case CANADA: return new EventDate(Month.JULY, 1, year);
            case CHINA: return new EventDate(Month.DECEMBER, 4, year);
            case COOK_ISLANDS: return new EventDate(Month.AUGUST, 4, year);
            case DENMARK: return new EventDate(Month.JUNE, 5, year);
            case DOMINICAN_REPUBLIC: return new EventDate(Month.NOVEMBER, 6, year);
            case ETHIOPIA: return new EventDate(Month.JULY, 16, year);
            case FIJI: return new EventDate(Month.SEPTEMBER, 7, year);
            case FINLAND: return new EventDate(Month.JULY, 17, year);
            case GERMANY: return new EventDate(Month.MAY, 23, year);
            case GHANA: return new EventDate(Month.JANUARY, 7, year);
            case INDIA: return new EventDate(Month.JANUARY, 26, year);
            case INDONESIA: return new EventDate(Month.AUGUST, 17, year);
            case IRELAND: return new EventDate(Month.DECEMBER, 29, year);
            case ITALY: return new EventDate(Month.JANUARY, 1, year);
            case JAPAN:
            case POLAND:
                return new EventDate(Month.MAY, 3, year);
            case KAZAKHSTAN: return new EventDate(Month.AUGUST, 30, year);
            case KYRGYZSTAN: return new EventDate(Month.MAY, 5, year);
            case LITHUANIA: return new EventDate(Month.OCTOBER, 25, year);
            case MALDIVES: return new EventDate(Month.DECEMBER, 22, year);
            case MEXICO: return getFirst(DayOfWeek.MONDAY, Month.FEBRUARY, year);
            case MONGOLIA: return new EventDate(Month.JANUARY, 13, year);
            case NETHERLANDS:
                final EventDate netherlands = new EventDate(Month.DECEMBER, 15, year);
                return netherlands.getLocalDate().getDayOfWeek() == DayOfWeek.SUNDAY ? netherlands.plusDays(1) : netherlands;
            case MICRONESIA: return new EventDate(Month.MAY, 10, year);
            case NORTH_KOREA: return new EventDate(Month.DECEMBER, 27, year);
            case NORWAY: return new EventDate(Month.MAY, 17, year);
            case PAKISTAN: return new EventDate(Month.MARCH, 23, year);
            case PHILIPPINES: return new EventDate(Month.FEBRUARY, 2, year);
            case PUERTO_RICO: return new EventDate(Month.JULY, 25, year);
            case ROMANIA:
            case UZBEKISTAN:
                return new EventDate(Month.DECEMBER, 8, year);
            case RUSSIA: return new EventDate(Month.DECEMBER, 12, year);
            case SERBIA: return new EventDate(Month.FEBRUARY, 15, year);
            case SLOVAKIA: return new EventDate(Month.SEPTEMBER, 1, year);
            case SOUTH_KOREA: return new EventDate(Month.JULY, 17, year);
            case SPAIN: return new EventDate(Month.DECEMBER, 6, year);
            case SWEDEN: return new EventDate(Month.JUNE, 6, year);
            case SWITZERLAND: return new EventDate(Month.SEPTEMBER, 12, year);
            case TAIWAN: return new EventDate(Month.DECEMBER, 25, year);
            case TAJIKISTAN: return new EventDate(Month.NOVEMBER, 6, year);
            case THAILAND: return new EventDate(Month.DECEMBER, 10, year);
            case TRANSNISTRIA: return new EventDate(Month.DECEMBER, 24, year);
            case TURKMENISTAN: return new EventDate(Month.MAY, 18, year);
            case UKRAINE: return new EventDate(Month.JUNE, 28, year);
            case UNITED_STATES: return new EventDate(Month.SEPTEMBER, 17, year);
            case URUGUAY: return new EventDate(Month.JULY, 18, year);
            case VANUATU: return new EventDate(Month.OCTOBER, 5, year);
        }
        return null;
    }
}
