package me.randomhashtags.worldlaws.observances.custom;

import me.randomhashtags.worldlaws.EventDate;
import me.randomhashtags.worldlaws.country.WLCountry;

import java.time.Month;

public enum AnzacDay implements CustomIHoliday {
    INSTANCE;

    @Override
    public String getOfficialName() {
        return "Anzac Day";
    }

    @Override
    public EventDate getDate(WLCountry country, int year) {
        switch (country) {
            case AUSTRALIA:
            case NEW_ZEALAND:
                return new EventDate(Month.APRIL, 25, year);
        }
        return null;
    }
}
