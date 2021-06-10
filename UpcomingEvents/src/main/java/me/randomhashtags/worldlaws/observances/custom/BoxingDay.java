package me.randomhashtags.worldlaws.observances.custom;

import me.randomhashtags.worldlaws.EventDate;
import me.randomhashtags.worldlaws.location.WLCountry;
import me.randomhashtags.worldlaws.observances.IHoliday;

import java.time.Month;

public enum BoxingDay implements IHoliday { // https://en.wikipedia.org/wiki/Boxing_Day
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
            case AUSTRALIA:
            case ANGUILLA:
            case BULGARIA:
            case CANADA:
            case CZECH_REPUBLIC:
            case FIJI:
            case GERMANY:
            case IRELAND:
            case JORDAN:
            case NETHERLANDS:
            case NEW_ZEALAND:
            case POLAND:
            case ROMANIA:
            case SCOTLAND:
            case SINGAPORE:
            case SLOVAKIA:
            case SOUTH_AFRICA:
            case TRINIDAD_AND_TOBAGO:
                return new EventDate(Month.DECEMBER, 26, year);
            case HONG_KONG:
            case MALAWI:
                final EventDate date1 = new EventDate(Month.DECEMBER, 26, year);
                switch (date1.getLocalDate().getDayOfWeek()) {
                    case SATURDAY: return date1.plusDays(2);
                    case SUNDAY: return date1.plusDays(1);
                    default: return date1;
                }
            case NIGERIA:
                return new EventDate(Month.DECEMBER, 25, year).getFirstWeekdayAfter();
            case UNITED_KINGDOM:
                final EventDate date = new EventDate(Month.DECEMBER, 26, year);
                switch (date.getLocalDate().getDayOfWeek()) {
                    case SATURDAY:
                    case SUNDAY:
                        return date.plusDays(2);
                    default:
                        return date;
                }
            default:
                return null;
        }
    }
}