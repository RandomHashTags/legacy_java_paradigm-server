package me.randomhashtags.worldlaws.info.service;

import java.util.HashSet;

public abstract class CountryServices {
    public static final HashSet<CountryService> STATIC_SERVICES = new HashSet<>();
    public static final HashSet<CountryService> NONSTATIC_SERVICES = new HashSet<>();
}
