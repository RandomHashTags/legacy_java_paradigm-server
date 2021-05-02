package me.randomhashtags.worldlaws.observances.holidays.custom;

import me.randomhashtags.worldlaws.EventDate;
import me.randomhashtags.worldlaws.location.WLCountry;
import me.randomhashtags.worldlaws.observances.holidays.IHoliday;

import java.time.Month;

public enum MartyrsDay implements IHoliday { // https://en.wikipedia.org/wiki/Martyrs%27_Day
    INSTANCE;

    @Override
    public Enum<? extends IHoliday> getEnum() {
        return null;
    }

    @Override
    public String getWikipediaName() {
        return "Martyrs' Day";
    }

    @Override
    public String[] getAliases() {
        return null;
    }

    public EventDate getDate(WLCountry country, int year) {
        switch (country) {
            case AFGHANISTAN: return new EventDate(Month.SEPTEMBER, 8, year);
            case ARMENIA: return new EventDate(Month.APRIL, 24, year);
            case AZERBAIJAN: return new EventDate(Month.JANUARY, 20, year);
            case BURKINA_FASO: return new EventDate(Month.OCTOBER, 31, year);
            case CHINA: return new EventDate(Month.SEPTEMBER, 30, year);
            case ERITREA: return new EventDate(Month.JUNE, 20, year);
            case INDIA: return new EventDate(Month.JANUARY, 30, year);
            case LEBANON:
            case SYRIA:
                return new EventDate(Month.MAY, 6, year);
            case LIBYA: return new EventDate(Month.SEPTEMBER, 16, year);
            case MADAGASCAR: return new EventDate(Month.MARCH, 29, year);
            case MALAWI: return new EventDate(Month.MARCH, 3, year);
            case MYANMAR: return new EventDate(Month.JULY, 19, year);
            case PAKISTAN: return new EventDate(Month.APRIL, 30, year);
            case PANAMA: return new EventDate(Month.JANUARY, 9, year);
            case SAO_TOME_AND_PRINCIPE: return new EventDate(Month.FEBRUARY, 3, year);
            case SOUTH_SUDAN: return new EventDate(Month.JULY, 30, year);
            case TOGO: return new EventDate(Month.JUNE, 21, year);
            case TUNISIA: return new EventDate(Month.APRIL, 9, year);
            case TURKEY: return new EventDate(Month.MARCH, 18, year);
            case VIETNAM: return new EventDate(Month.JULY, 27, year);
            case UGANDA: return new EventDate(Month.JUNE, 3, year);
            case UNITED_ARAB_EMIRATES: return new EventDate(Month.NOVEMBER, 30, year);
            default: return null;
        }
    }
}
