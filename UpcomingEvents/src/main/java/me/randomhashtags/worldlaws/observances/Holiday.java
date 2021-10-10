package me.randomhashtags.worldlaws.observances;

import me.randomhashtags.worldlaws.EventSources;

import java.util.HashSet;

public interface Holiday {
    HashSet<String> getCountries();
    String getEnglishName();
    String[] getAliases();
    String getDescription();
    EventSources getSources();
}
