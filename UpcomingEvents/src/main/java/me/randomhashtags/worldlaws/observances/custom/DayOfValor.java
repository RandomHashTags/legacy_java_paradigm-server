package me.randomhashtags.worldlaws.observances.custom;

import me.randomhashtags.worldlaws.EventDate;
import me.randomhashtags.worldlaws.country.WLCountry;

import java.time.Month;

public enum DayOfValor implements CustomHoliday {
    INSTANCE;

    @Override
    public String getWikipediaName() {
        return "Day of Valor";
    }

    @Override
    public String[] getAliases() {
        return collectAliases("Bataan Day", "Bataan and Corregidor Day");
    }

    @Override
    public EventDate getDate(WLCountry country, int year) {
        switch (country) {
            case PHILIPPINES: return new EventDate(Month.APRIL, 9, year);
        }
        return null;
    }
}
