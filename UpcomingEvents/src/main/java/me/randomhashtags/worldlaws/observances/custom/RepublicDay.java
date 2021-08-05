package me.randomhashtags.worldlaws.observances.custom;

import me.randomhashtags.worldlaws.EventDate;
import me.randomhashtags.worldlaws.country.WLCountry;
import me.randomhashtags.worldlaws.observances.IHoliday;

import java.time.Month;

public enum RepublicDay implements IHoliday { // https://en.wikipedia.org/wiki/Republic_Day
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
        return null;
    }

    public EventDate getDate(WLCountry country, int year) {
        switch (country) {
            case SWITZERLAND: return new EventDate(Month.MARCH, 1, year);
        }
        return null;
    }
}
