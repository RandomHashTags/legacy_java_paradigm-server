package me.randomhashtags.worldlaws.history;

import me.randomhashtags.worldlaws.EventSources;
import me.randomhashtags.worldlaws.service.wikipedia.WikipediaService;

public interface ICountryHistory extends WikipediaService {
    CountryHistorySection getEras();
    EventSources getSources();
}
