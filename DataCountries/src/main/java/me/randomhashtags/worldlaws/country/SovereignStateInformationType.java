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
    public boolean isArray(boolean isCountry) {
        switch (this) {
            case CURRENCIES:
                return true;
            case NEIGHBORS:
                return isCountry;
            default:
                return false;
        }
    }
}
