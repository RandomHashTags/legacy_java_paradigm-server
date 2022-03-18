package me.randomhashtags.worldlaws.observances.custom;

import me.randomhashtags.worldlaws.EventSource;
import me.randomhashtags.worldlaws.EventSources;
import me.randomhashtags.worldlaws.country.WLCountry;
import me.randomhashtags.worldlaws.observances.Holiday;
import me.randomhashtags.worldlaws.observances.HolidayType;

public interface CustomHoliday extends Holiday {

    @Override
    default HolidayType getType() {
        return null;
    }

    @Override
    default String[] getAliases() {
        return null;
    }

    @Override
    default EventSources getSources(WLCountry country) {
        final String wikipediaName = getWikipediaName();
        final String url = "https://en.wikipedia.org/wiki/" + wikipediaName.replace(" ", "_");
        final EventSource source = new EventSource(WIKIPEDIA + wikipediaName, url);
        return collectSources(source);
    }
}
