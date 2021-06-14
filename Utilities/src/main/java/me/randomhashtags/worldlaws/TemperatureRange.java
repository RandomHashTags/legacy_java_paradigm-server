package me.randomhashtags.worldlaws;

public final class TemperatureRange {
    private final TemperatureUnit unit;
    private final String lowest, lowAverage, average, highAverage, highest;

    public TemperatureRange(TemperatureUnit unit, String lowest, String lowAverage, String average, String highAverage, String highest) {
        this.unit = unit;
        this.lowest = lowest;
        this.lowAverage = lowAverage;
        this.average = average;
        this.highAverage = highAverage;
        this.highest = highest;
    }

    @Override
    public String toString() {
        return "{" +
                "\"unit\":\"" + unit.name() + "\"," +
                "\"lowest\":\"" + lowest + "\"," +
                "\"lowAverage\":\"" + lowAverage + "\"," +
                "\"average\":\"" + average + "\"," +
                "\"highAverage\":\"" + highAverage + "\"," +
                "\"highest\":\"" + highest + "\"" +
                "}";
    }
}
