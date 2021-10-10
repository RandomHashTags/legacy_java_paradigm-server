package me.randomhashtags.worldlaws.observances.custom;

import me.randomhashtags.worldlaws.EventDate;
import me.randomhashtags.worldlaws.EventSource;
import me.randomhashtags.worldlaws.EventSources;
import me.randomhashtags.worldlaws.country.WLCountry;

import java.time.DayOfWeek;
import java.time.Month;

public enum VeteransDay implements CustomIHoliday {
    INSTANCE;

    @Override
    public String getOfficialName() {
        return "Veterans' Day";
    }

    @Override
    public String[] getAliases() {
        return collectAliases("Armed Forces Day", "Armistice Day");
    }

    @Override
    public EventDate getDate(WLCountry country, int year) {
        switch (country) {
            case FINLAND: return new EventDate(Month.APRIL, 27, year);
            case NETHERLANDS:
            case UNITED_KINGDOM:
                return getLast(DayOfWeek.SATURDAY, Month.JUNE, year);
            case NORWAY: return new EventDate(Month.MAY, 8, year);
            case SOUTH_KOREA: return new EventDate(Month.OCTOBER, 8, year);
            case SWEDEN: return new EventDate(Month.MAY, 29, year);
            case UNITED_STATES: return new EventDate(Month.NOVEMBER, 11, year);
            default: return null;
        }
    }

    @Override
    public EventSources getSources(WLCountry country) {
        switch (country) {
            case FINLAND:
                return collectSources(new EventSource(WIKIPEDIA + "National Veterans' Day", "https://en.wikipedia.org/wiki/National_Veterans%27_Day"));
            case NETHERLANDS:
                return collectSources(new EventSource(WIKIPEDIA + "Veterans' Day (Netherlands)", "https://en.wikipedia.org/wiki/Veterans%27_Day_(Netherlands)"));
            case NORWAY:
                return collectSources(new EventSource(WIKIPEDIA + "Veterans Day (Norway)", "https://en.wikipedia.org/wiki/Veterans_Day_(Norway)"));
            case SOUTH_KOREA:
                return collectSources(new EventSource(WIKIPEDIA + "Veterans Day (South Korea)", "https://en.wikipedia.org/wiki/Veterans_Day_(South_Korea)"));
            case SWEDEN:
                return collectSources(new EventSource(WIKIPEDIA + "Veterans Day (Sweden)", "https://en.wikipedia.org/wiki/Veterans_Day_(Sweden)"));
            case UNITED_KINGDOM:
                return collectSources(new EventSource(WIKIPEDIA + "Armed Forces Day (United Kingdom)", "https://en.wikipedia.org/wiki/Armed_Forces_Day_(United_Kingdom)"));
            default:
                return getDefaultSources();
        }
    }
}
