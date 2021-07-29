package me.randomhashtags.worldlaws.location.history;

import me.randomhashtags.worldlaws.service.WikipediaService;

import java.util.List;

public interface ICountryHistory extends WikipediaService {
    List<CountryHistorySection> get();
}
