package me.randomhashtags.worldlaws.service;

import java.util.HashSet;

public abstract class CountryServices {
    public static final HashSet<CountryService> STATIC_SERVICES = new HashSet<>();
    public static final HashSet<CountryService> NONSTATIC_SERVICES = new HashSet<>();

    public static final HashSet<NewCountryService> NEW_STATIC_SERVICES = new HashSet<>();
    public static final HashSet<NewCountryService> NEW_NONSTATIC_SERVICES = new HashSet<>();
}
