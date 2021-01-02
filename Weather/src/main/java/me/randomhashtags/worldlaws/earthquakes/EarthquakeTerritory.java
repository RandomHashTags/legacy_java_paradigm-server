package me.randomhashtags.worldlaws.earthquakes;

import me.randomhashtags.worldlaws.LocalServer;

import java.time.Month;

public final class EarthquakeTerritory {
    private int year, totalEarthquakes;
    private Month month;
    private String territory;

    public EarthquakeTerritory(int year, Month month, String territory, int totalEarthquakes) {
        this.year = year;
        this.month = month;
        this.territory = LocalServer.fixEscapeValues(territory);
        this.totalEarthquakes = totalEarthquakes;
    }

    public int getYear() {
        return year;
    }
    public Month getMonth() {
        return month;
    }
    public String getTerritory() {
        return territory;
    }
    public int getTotalEarthquakes() {
        return totalEarthquakes;
    }

    @Override
    public String toString() {
        return "{" +
                "\"year\":" + year + "," +
                "\"month\":" + month.getValue() + "," +
                "\"territory\":\"" + territory + "\"," +
                "\"totalEarthquakes\":" + totalEarthquakes +
                "}";
    }
}
