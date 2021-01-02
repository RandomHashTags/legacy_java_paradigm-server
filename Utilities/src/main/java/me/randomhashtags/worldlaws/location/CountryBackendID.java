package me.randomhashtags.worldlaws.location;

public interface CountryBackendID {
    String getValue();

    CountryBackendID AUSTRALIA = () -> "australia";
    CountryBackendID CANADA = () -> "canada";
    CountryBackendID UNITED_STATES = () -> "unitedstates";
}
