package me.randomhashtags.worldlaws.observances.type.unfinished;

import me.randomhashtags.worldlaws.EventDate;
import me.randomhashtags.worldlaws.country.WLCountry;
import me.randomhashtags.worldlaws.observances.Holiday;
import me.randomhashtags.worldlaws.observances.HolidayType;

public enum JewishHoliday implements Holiday {

    HANUKKAH,
    SUKKOT,
    YOM_KIPPUR,

    ;

    private final String wikipediaName;

    JewishHoliday() {
        this(null);
    }
    JewishHoliday(String wikipediaName) {
        this.wikipediaName = wikipediaName;
    }

    @Override
    public HolidayType getType() {
        return null;
    }

    @Override
    public String getWikipediaName() {
        return wikipediaName;
    }

    @Override
    public String[] getAliases() {
        switch (this) {
            case HANUKKAH: return collectAliases("Festival of Lights");
            default: return null;
        }
    }

    @Override
    public EventDate getDate(WLCountry country, int year) {
        switch (this) {
            case HANUKKAH: return null;
        }
        return null;
    }
}
