package me.randomhashtags.worldlaws.observances;

import java.util.HashSet;

public interface Holiday {
    HashSet<String> getCountries();
    String getEnglishName();
    String[] getAliases();
    String getDescription();
    String getLearnMoreURL();
}
