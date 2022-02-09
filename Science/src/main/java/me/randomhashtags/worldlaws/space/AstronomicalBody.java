package me.randomhashtags.worldlaws.space;

import me.randomhashtags.worldlaws.EventSources;

public interface AstronomicalBody {
    String getName();
    String[] getAliases();
    String getDescription();
    String getRangeFromEarth();
    String[] getImageURLs();
    EventSources getSources();
}
