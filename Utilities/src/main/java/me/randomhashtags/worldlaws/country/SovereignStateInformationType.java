package me.randomhashtags.worldlaws.country;

public enum SovereignStateInformationType {
    AGRICULTURE,
    AVAILABILITIES,
    ENVIRONMENT,
    GEOGRAPHY,
    HISTORY,
    INFORMATION,
    LEGALITIES,
    LISTS,
    NATIONAL,
    POLITICS,
    RANKINGS,
    RESOURCES_NONSTATIC,
    RESOURCES_STATIC,
    SERVICES_STATIC,
    SERVICES_NONSTATIC,
    SINGLE_VALUES,

    CURRENCIES,
    NEIGHBORS,
    ;

    public String getName() {
        return name();
    }

    public boolean isNonStatic() {
        return name().endsWith("NONSTATIC");
    }
    public boolean isArray() {
        switch (this) {
            case CURRENCIES:
            case NEIGHBORS:
                return true;
            default:
                return false;
        }
    }
}
