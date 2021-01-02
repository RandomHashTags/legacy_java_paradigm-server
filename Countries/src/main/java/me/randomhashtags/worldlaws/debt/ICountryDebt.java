package me.randomhashtags.worldlaws.debt;

import me.randomhashtags.worldlaws.CompletionHandler;
import me.randomhashtags.worldlaws.RestAPI;
import me.randomhashtags.worldlaws.location.CountryBackendID;

public interface ICountryDebt extends RestAPI {
    CountryBackendID getCountryBackendID();
    void getCurrentJSON(CompletionHandler handler);
    void getFromYear(int year, CompletionHandler handler);
}
