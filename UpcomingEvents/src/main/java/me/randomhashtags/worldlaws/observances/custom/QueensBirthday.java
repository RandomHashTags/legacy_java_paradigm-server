package me.randomhashtags.worldlaws.observances.custom;

import me.randomhashtags.worldlaws.EventDate;
import me.randomhashtags.worldlaws.country.WLCountry;

import java.time.DayOfWeek;
import java.time.Month;

public enum QueensBirthday implements CustomHoliday {
    INSTANCE;

    @Override
    public String getWikipediaName() {
        return "Queen's Official Birthday";
    }

    @Override
    public String[] getAliases() {
        return collectAliases("Queen's Birthday");
    }

    @Override
    public EventDate getDate(WLCountry country, int year) {
        switch (country) {
            case ANGUILLA:
            case AUSTRALIA:
            case CAYMAN_ISLANDS:
            case COOK_ISLANDS:
            case GIBRALTAR:
            case MONTSERRAT:
            case PAPUA_NEW_GUINEA:
            case SAINT_HELENA_ASCENSION_AND_TRISTAN_DA_CUNHA:
            case SOLOMON_ISLANDS:
            case TURKS_AND_CAICOS_ISLANDS:
                return EventDate.getSecond(DayOfWeek.MONDAY, Month.JUNE, year);
            case FALKLAND_ISLANDS: return new EventDate(Month.APRIL, 21, year);
            case NEW_ZEALAND: return EventDate.getFirst(DayOfWeek.MONDAY, Month.JUNE, year);
            case TUVALU:
            case UNITED_KINGDOM:
                return EventDate.getSecond(DayOfWeek.SATURDAY, Month.JUNE, year);
        }
        return null;
    }
}
