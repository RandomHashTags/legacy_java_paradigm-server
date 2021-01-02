package me.randomhashtags.worldlaws.location;

import me.randomhashtags.worldlaws.CompletionHandler;

public interface ICountry {
    CountryBackendID getCountryBackendID();
    void getResponse(String value, CompletionHandler handler);
}
