package me.randomhashtags.worldlaws.observances.custom;

import me.randomhashtags.worldlaws.EventDate;
import me.randomhashtags.worldlaws.country.WLCountry;

import java.time.Month;

public enum MayDay implements CustomIHoliday {
    INSTANCE;

    @Override
    public String getOfficialName() {
        return "May Day";
    }

    @Override
    public String[] getAliases() {
        return collectAliases("Labour Day");
    }

    @Override
    public EventDate getDate(WLCountry country, int year) {
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
            default:
                return null;
        }
    }
}
