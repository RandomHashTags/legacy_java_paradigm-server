package me.randomhashtags.worldlaws.location;

public enum TerritoryInformationType implements InformationType {
    AVAILABILITIES,
    INFORMATION,
    LEGALITIES,
    RESOURCES,
    ;

    @Override
    public String getName() {
        return name();
    }
}
