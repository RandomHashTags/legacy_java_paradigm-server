package me.randomhashtags.worldlaws.country;

import me.randomhashtags.worldlaws.service.WikipediaService;

public interface ICountryHistory extends WikipediaService {
    CountryHistorySection getEras();
}
