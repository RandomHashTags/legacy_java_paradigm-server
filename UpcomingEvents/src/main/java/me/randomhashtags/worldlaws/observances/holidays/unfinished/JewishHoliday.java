package me.randomhashtags.worldlaws.observances.holidays.unfinished;

import me.randomhashtags.worldlaws.EventDate;
import me.randomhashtags.worldlaws.location.WLCountry;
import me.randomhashtags.worldlaws.observances.holidays.IHoliday;

public enum JewishHoliday implements IHoliday {

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
    public Enum<? extends IHoliday> getEnum() {
        return this;
    }

    @Override
    public String getWikipediaName() {
        return wikipediaName;
    }

    @Override
    public String[] getAliases() {
        return null;
    }

    @Override
    public EventDate getDate(WLCountry country, int year) {
        switch (this) {
        }
        return null;
    }
}
