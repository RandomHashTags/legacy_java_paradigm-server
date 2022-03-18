package me.randomhashtags.worldlaws.observances.custom;

import me.randomhashtags.worldlaws.EventDate;
import me.randomhashtags.worldlaws.country.WLCountry;

import java.time.Month;

public enum BoxingDay implements CustomHoliday {
    INSTANCE;

    @Override
    public String getWikipediaName() {
        return "Boxing Day";
    }

    @Override
    public String[] getAliases() {
        return collectAliases("Offering Day");
    }

    @Override
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
