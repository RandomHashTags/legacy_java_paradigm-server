package me.randomhashtags.worldlaws.earthquakes.recode;

public final class TerritoryEarthquakes {
    private final String recent;

    public TerritoryEarthquakes(String recent) {
        this.recent = recent;
    }

    @Override
    public String toString() {
        return "{" +
                "\"recent\":" + recent +
                "}";
    }
}
