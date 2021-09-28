package me.randomhashtags.worldlaws.energy;

public enum Renewables {
    GEOTHERMAL_ENERGY("https://en.wikipedia.org/wiki/Geothermal_energy"),
    HYDROELECTRIC_POWER("https://en.wikipedia.org/wiki/Hydroelectricity"),
    NUCLEAR_ENERGY("https://en.wikipedia.org/wiki/Nuclear_power"),
    SOLAR_ENERGY("https://en.wikipedia.org/wiki/Solar_energy"),
    WIND_ENERGY("https://en.wikipedia.org/wiki/Wind_power"),
    ;

    private final String wikipageURL;

    Renewables(String wikipageURL) {
        this.wikipageURL = wikipageURL;
    }
}
