package me.randomhashtags.worldlaws.service;

import java.util.HashSet;

public abstract class CountryServices {
    public static final HashSet<NewCountryService> STATIC_SERVICES = new HashSet<>();
    public static final HashSet<NewCountryServiceNonStatic> NONSTATIC_SERVICES = new HashSet<>();
}
