package me.randomhashtags.worldlaws.observances.holidays.custom;

import me.randomhashtags.worldlaws.EventDate;
import me.randomhashtags.worldlaws.location.WLCountry;
import me.randomhashtags.worldlaws.observances.holidays.IHoliday;

import java.time.DayOfWeek;
import java.time.Month;

public enum TeachersDay implements IHoliday {
    INSTANCE;

    @Override
    public Enum<? extends IHoliday> getEnum() {
        return null;
    }

    @Override
    public String getWikipediaName() {
        return null;
    }

    @Override
    public String[] getAliases() {
        return null;
    }

    public EventDate getDate(WLCountry country, int year) {
        switch (country) {
            case ALBANIA:
            case KOSOVO:
                return new EventDate(Month.MARCH, 7, year);
            case ARGENTINA: return new EventDate(Month.SEPTEMBER, 11, year);
            case ARMENIA:
            case AZERBAIJAN:
            case BAHRAIN:
            case BANGLADESH:
            case BELGIUM:
            case BULGARIA:
            case CAMEROON:
            case CANADA:
            case CROATIA:
            case ESTONIA:
            case GEORGIA:
            case GERMANY:
            case KUWAIT:
            case LITHUANIA:
            case MALDIVES:
            case MAURITIUS:
            case MOLDOVA:
            case MONGOLIA:
            case MYANMAR:
            case NETHERLANDS:
            case NIGERIA:
            case NORTH_MACEDONIA:
            case PAKISTAN:
            case PAPUA_NEW_GUINEA:
            case PHILIPPINES:
            case PORTUGAL:
            case QATAR:
            case ROMANIA:
            case RUSSIA:
            case SAUDI_ARABIA:
            case SERBIA:
            case UNITED_ARAB_EMIRATES:
            case UNITED_KINGDOM:
                return new EventDate(Month.OCTOBER, 5, year);
            case AUSTRALIA:
                final EventDate day = getLast(DayOfWeek.FRIDAY, Month.OCTOBER, year);
                return day.getDay() == 31 ? new EventDate(Month.NOVEMBER, 7, year) : day;
            case BELARUS:
            case KAZAKHSTAN:
            case LATVIA:
            case UKRAINE:
                return getFirst(DayOfWeek.SUNDAY, Month.OCTOBER, year);
            case BHUTAN:
            case IRAN:
                return new EventDate(Month.MAY, 2, year);
            case BOLIVIA: return new EventDate(Month.JUNE, 6, year);
            case BRAZIL: return new EventDate(Month.OCTOBER, 15, year);
            case BRUNEI: return new EventDate(Month.SEPTEMBER, 23, year);
            case CHILE: return new EventDate(Month.OCTOBER, 16, year);
            case CHINA:
            case HONG_KONG:
                return new EventDate(Month.SEPTEMBER, 10, year);
            case COLOMBIA: return new EventDate(Month.MAY, 15, year);
            case COSTA_RICA:
            case CUBA:
                return new EventDate(Month.DECEMBER, 22, year);
            case CZECH_REPUBLIC: return new EventDate(Month.MARCH, 28, year);
            case DOMINICAN_REPUBLIC: return new EventDate(Month.JUNE, 30, year);
            case ECUADOR: return new EventDate(Month.APRIL, 13, year);
            case EGYPT:
            case JORDAN:
            case LIBYA:
            case MOROCCO:
            case TUNISIA:
            case YEMEN:
                return new EventDate(Month.FEBRUARY, 28, year);
            case EL_SALVADOR: return new EventDate(Month.JUNE, 22, year);
            case GREECE: return new EventDate(Month.JANUARY, 30, year);
            case GUATEMALA: return new EventDate(Month.JUNE, 25, year);
            case HONDURAS: return new EventDate(Month.SEPTEMBER, 17, year);
            case HUNGARY: return getFirst(DayOfWeek.SUNDAY, Month.JUNE, year);
            case INDONESIA: return new EventDate(Month.NOVEMBER, 25, year);
            case IRAQ: return new EventDate(Month.MARCH, 1, year);
            case JAMAICA: return new EventDate(Month.MAY, 6, year);
            case LAOS: return new EventDate(Month.OCTOBER, 7, year);
            case LEBANON: return new EventDate(Month.MARCH, 9, year);
            case MALAYSIA: return new EventDate(Month.MAY, 16, year);
            case MEXICO:
            case SOUTH_KOREA:
                return new EventDate(Month.MAY, 15, year);
            case NEW_ZEALAND: return new EventDate(Month.OCTOBER, 29, year);
            case OMAN: return new EventDate(Month.FEBRUARY, 24, year);
            case PALESTINE: return new EventDate(Month.DECEMBER, 14, year);
            case PANAMA:
            case SOUTH_SUDAN:
                return new EventDate(Month.DECEMBER, 1, year);
            case PARAGUAY: return new EventDate(Month.APRIL, 30, year);
            case PERU: return new EventDate(Month.JULY, 6, year);
            case POLAND: return new EventDate(Month.OCTOBER, 14, year);
            case SINGAPORE: return getFirst(DayOfWeek.FRIDAY, Month.SEPTEMBER, year);
            case SLOVAKIA: return new EventDate(Month.MARCH, 28, year);
            case SOMALIA: return new EventDate(Month.NOVEMBER, 21, year);
            case SPAIN: return new EventDate(Month.NOVEMBER, 27, year);
            case SRI_LANKA: return new EventDate(Month.OCTOBER, 6, year);
            case SYRIA: return new EventDate(Month.MARCH, 18, year);
            case TAIWAN: return new EventDate(Month.SEPTEMBER, 28, year);
            case THAILAND: return new EventDate(Month.JANUARY, 16, year);
            case TURKEY: return new EventDate(Month.NOVEMBER, 24, year);
            case URUGUAY: return new EventDate(Month.SEPTEMBER, 22, year);
            case UZBEKISTAN: return new EventDate(Month.OCTOBER, 1, year);
            case VENEZUELA: return new EventDate(Month.JANUARY, 15, year);
            case VIETNAM: return new EventDate(Month.NOVEMBER, 20, year);
        }
        return null;
    }
}
