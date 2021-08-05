package me.randomhashtags.worldlaws.country;

import me.randomhashtags.worldlaws.service.WikipediaService;

import java.util.List;

public interface ICountryHistory extends WikipediaService {
    List<CountryHistorySection> get();
}
