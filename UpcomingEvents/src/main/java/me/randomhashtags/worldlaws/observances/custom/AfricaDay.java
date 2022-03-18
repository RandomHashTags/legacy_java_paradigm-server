package me.randomhashtags.worldlaws.observances.custom;

import me.randomhashtags.worldlaws.EventDate;
import me.randomhashtags.worldlaws.country.WLCountry;

import java.time.Month;

public enum AfricaDay implements CustomHoliday {
    INSTANCE;

    @Override
    public String getWikipediaName() {
        return "Africa Day";
    }

    @Override
    public String[] getAliases() {
        return collectAliases(
                "African Freedom Day",
                "African Liberation Day"
        );
    }

    @Override
    public EventDate getDate(WLCountry country, int year) {
        switch (country) {
            case MAURITANIA: return new EventDate(Month.MAY, 25, year);
        }
        return null;
    }
}
