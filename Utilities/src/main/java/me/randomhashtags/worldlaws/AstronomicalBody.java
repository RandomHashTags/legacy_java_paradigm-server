package me.randomhashtags.worldlaws;

public interface AstronomicalBody {
    String getName();
    String[] getAliases();
    String getDescription();
    String getRangeFromEarth();
    String[] getImageURLs();
    EventSources getSources();
}
