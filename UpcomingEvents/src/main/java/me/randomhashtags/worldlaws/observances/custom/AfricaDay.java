package me.randomhashtags.worldlaws.observances.custom;

import me.randomhashtags.worldlaws.EventDate;
import me.randomhashtags.worldlaws.location.WLCountry;
import me.randomhashtags.worldlaws.observances.IHoliday;

import java.time.Month;

public enum AfricaDay implements IHoliday { // hhttps://en.wikipedia.org/wiki/Africa_Day
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
        return new String[] {
                "African Freedom Day",
                "African Liberation Day"
        };
    }

    public EventDate getDate(WLCountry country, int year) {
        switch (country) {
            case MAURITANIA: return new EventDate(Month.MAY, 25, year);
        }
        return null;
    }
}
