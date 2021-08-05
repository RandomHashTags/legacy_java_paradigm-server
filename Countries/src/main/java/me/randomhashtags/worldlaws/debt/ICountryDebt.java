package me.randomhashtags.worldlaws.debt;

import me.randomhashtags.worldlaws.CompletionHandler;
import me.randomhashtags.worldlaws.RestAPI;
import me.randomhashtags.worldlaws.country.WLCountry;

public interface ICountryDebt extends RestAPI {
    WLCountry getCountryBackendID();
    void getCurrentJSON(CompletionHandler handler);
    void getFromYear(int year, CompletionHandler handler);
}
