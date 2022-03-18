package me.randomhashtags.worldlaws.observances.custom;

import me.randomhashtags.worldlaws.EventDate;
import me.randomhashtags.worldlaws.country.WLCountry;

import java.time.Month;

public enum EmancipationDay implements CustomHoliday {
    INSTANCE;

    @Override
    public String getWikipediaName() {
        return "Emancipation Day";
    }

    @Override
    public EventDate getDate(WLCountry country, int year) {
        switch (country) {
            case ANTIGUA_AND_BARBUDA:
            case ANGUILLA:
            case BAHAMAS:
            case BARBADOS:
            case BELIZE:
            case BERMUDA:
            case BRITISH_VIRGIN_ISLANDS:
            case CANADA:
            case DOMINICA:
            case GRENADA:
            case JAMAICA:
            case SAINT_KITTS_AND_NEVIS:
            case SAINT_VINCENT_AND_THE_GRENADINES:
            case SOUTH_AFRICA:
            case TRINIDAD_AND_TOBAGO:
                return new EventDate(Month.JULY, 31, year);
            case GUADELOUPE:
                return new EventDate(Month.MAY, 27, year);
            case PUERTO_RICO:
                return new EventDate(Month.MARCH, 22, year);
            case SURINAME:
                return new EventDate(Month.JULY, 1, year);
            default:
                return null;
        }
    }
}
