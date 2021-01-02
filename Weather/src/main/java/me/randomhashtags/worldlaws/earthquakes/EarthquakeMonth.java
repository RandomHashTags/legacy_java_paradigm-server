package me.randomhashtags.worldlaws.earthquakes;

import java.time.Month;

public final class EarthquakeMonth {
    private int year, totalEarthquakes;
    private Month month;

    public EarthquakeMonth(int year, Month month, int totalEarthquakes) {
        this.year = year;
        this.month = month;
        this.totalEarthquakes = totalEarthquakes;
    }
    public Month getMonth() {
        return month;
    }
    public int getTotalEarthquakes() {
        return totalEarthquakes;
    }

    @Override
    public String toString() {
        return "{" +
                "\"year\":" + year + "," +
                "\"month\":" + month.getValue() + "," +
                "\"totalEarthquakes\":" + totalEarthquakes +
                "}";
    }
}
