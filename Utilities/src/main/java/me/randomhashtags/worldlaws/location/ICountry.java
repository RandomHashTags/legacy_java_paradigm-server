package me.randomhashtags.worldlaws.location;

import me.randomhashtags.worldlaws.CompletionHandler;

public interface ICountry {
    WLCountry getCountryBackendID();
    void getResponse(String value, CompletionHandler handler);
}
