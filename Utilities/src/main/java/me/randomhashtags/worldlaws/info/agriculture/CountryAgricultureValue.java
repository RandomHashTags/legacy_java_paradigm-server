package me.randomhashtags.worldlaws.info.agriculture;

import me.randomhashtags.worldlaws.EventSources;
import me.randomhashtags.worldlaws.LocalServer;
import me.randomhashtags.worldlaws.NumberType;

public final class CountryAgricultureValue {
    private final int maxWorldRank, yearOfData;
    protected int worldRank;
    private final Number value;
    private final NumberType valueType;
    private final boolean isEstimate;
    private final String description, suffix;
    private final EventSources sources;

    public CountryAgricultureValue(int maxWorldRank, int worldRank, int yearOfData, Number value, NumberType valueType, boolean isEstimate, String description, String suffix, EventSources sources) {
        this.maxWorldRank = maxWorldRank;
        this.worldRank = worldRank;
        this.yearOfData = yearOfData;
        this.value = value;
        this.valueType = valueType;
        this.isEstimate = isEstimate;
        this.description = description;
        this.suffix = suffix;
        this.sources = sources;
    }

    public int getMaxWorldRank() {
        return maxWorldRank;
    }
    public int getWorldRank() {
        return worldRank;
    }
    public int getYearOfData() {
        return yearOfData;
    }
    public Number getValue() {
        return value;
    }
    public NumberType getValueType() {
        return valueType;
    }
    public boolean isEstimate() {
        return isEstimate;
    }
    public String getDescription() {
        return description;
    }
    public String getSuffix() {
        return suffix;
    }
    public EventSources getSources() {
        return sources;
    }

    @Override
    public String toString() {
        return "{" +
                "\"maxWorldRank\":" + maxWorldRank + "," +
                "\"worldRank\":" + worldRank + "," +
                "\"yearOfData\":" + yearOfData + "," +
                "\"value\":" + value + "," +
                "\"valueType\":\"" + valueType.name() + "\"," +
                "\"isEstimate\":" + isEstimate + "," +
                "\"description\":\"" + LocalServer.fixEscapeValues(description) + "\"," +
                "\"suffix\":\"" + LocalServer.fixEscapeValues(suffix) + "\"," +
                "\"sources\":" + sources.toString() +
                "}";
    }
}
