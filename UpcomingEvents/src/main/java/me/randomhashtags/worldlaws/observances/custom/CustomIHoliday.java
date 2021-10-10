package me.randomhashtags.worldlaws.observances.custom;

import me.randomhashtags.worldlaws.EventSource;
import me.randomhashtags.worldlaws.EventSources;
import me.randomhashtags.worldlaws.country.WLCountry;
import me.randomhashtags.worldlaws.observances.IHoliday;

public interface CustomIHoliday extends IHoliday {

    @Override
    default Enum<? extends IHoliday> getEnum() {
        return null;
    }

    @Override
    default EventSources getSources(WLCountry country) {
        final String url = "https://en.wikipedia.org/wiki/" + getOfficialName().replace(" ", "_");
        final EventSource source = new EventSource(WIKIPEDIA + getOfficialName(), url);
        return collectSources(source);
    }
}
