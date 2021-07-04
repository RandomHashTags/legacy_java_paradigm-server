package me.randomhashtags.worldlaws.location;

public enum CountryInformationType implements InformationType {
    AGRICULTURE,
    AVAILABILITIES,
    INFORMATION,
    LEGALITIES,
    LISTS,
    NATIONAL,
    POLITICS,
    RANKINGS,
    RESOURCES,
    SERVICES,
    SINGLE_VALUES,
    TERRITORIES,
    ;

    @Override
    public String getName() {
        return name();
    }
}
