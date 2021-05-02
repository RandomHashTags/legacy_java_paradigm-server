package me.randomhashtags.worldlaws.observances.holidays.custom;

import me.randomhashtags.worldlaws.EventDate;
import me.randomhashtags.worldlaws.location.WLCountry;
import me.randomhashtags.worldlaws.observances.holidays.IHoliday;

import java.time.DayOfWeek;
import java.time.Month;

public enum MothersDay implements IHoliday {
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
            case ARMENIA:
            case AZERBAIJAN:
                return new EventDate(Month.MARCH, 8, year);
            case ANGUILLA:
            case ANTIGUA_AND_BARBUDA:
            case AUSTRALIA:
            case AUSTRIA:
            case BAHAMAS:
            case BANGLADESH:
            case BELGIUM:
            case BRAZIL:
            case CANADA:
            case CHINA:
            case CZECH_REPUBLIC:
            case ESTONIA:
            case FINLAND:
            case GERMANY:
            case INDIA:
            case ITALY:
            case JAPAN:
            case LATVIA:
            case MALTA:
            case NETHERLANDS:
            case NEW_ZEALAND:
            case PAKISTAN:
            case PHILIPPINES:
            case SAMOA:
            case SINGAPORE:
            case SLOVAKIA:
            case SRI_LANKA:
            case SWITZERLAND:
            case TAIWAN:
            case UKRAINE:
            case UNITED_STATES:
                return getSecond(DayOfWeek.SUNDAY, Month.MAY, year);
            case MEXICO:
                return new EventDate(Month.MAY, 10, year);
            case BELARUS: return new EventDate(Month.OCTOBER, 14, year);
            case BENIN: return new EventDate(Month.MAY, 14, year);
            case BHUTAN: return new EventDate(Month.MAY, 8, year);
            case BOLIVIA: return new EventDate(Month.MAY, 27, year);

            case EGYPT: return new EventDate(Month.MARCH, 21, year);
            case GEORGIA: return new EventDate(Month.MARCH, 3, year);

            case HUNGARY:
            case LITHUANIA:
            case PARAGUAY:
                return new EventDate(Month.MAY, 15, year);

            case INDONESIA: return new EventDate(Month.DECEMBER, 22, year);
            case KYRGYZSTAN: return new EventDate(Month.MAY, 19, year);
            case MALAWI: return new EventDate(Month.OCTOBER, 15, year);
            case MALDIVES: return new EventDate(Month.MAY, 13, year);
            case NICARAGUA: return new EventDate(Month.MAY, 30, year);
            case NORTH_KOREA: return new EventDate(Month.NOVEMBER, 16, year);
            case NORWAY: return getSecond(DayOfWeek.SUNDAY, Month.FEBRUARY, year);
            case PANAMA: return new EventDate(Month.DECEMBER, 8, year);
            case PORTUGAL:
            case ROMANIA:
            case SPAIN:
                return getFirst(DayOfWeek.SUNDAY, Month.MAY, year);
            case RUSSIA:
            case TRANSNISTRIA:
                return getLast(DayOfWeek.SUNDAY, Month.NOVEMBER, year);
            case SOUTH_SUDAN: return getFirst(DayOfWeek.MONDAY, Month.JULY, year);
            case THAILAND: return new EventDate(Month.AUGUST, 12, year);
        }
        return null;
    }
}
