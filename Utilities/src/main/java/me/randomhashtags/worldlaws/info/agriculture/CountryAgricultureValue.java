package me.randomhashtags.worldlaws.info.agriculture;

import me.randomhashtags.worldlaws.EventSources;
import me.randomhashtags.worldlaws.LocalServer;
import me.randomhashtags.worldlaws.NumberType;
import me.randomhashtags.worldlaws.ServerObject;
import org.json.JSONObject;

public final class CountryAgricultureValue implements ServerObject {
    public String country;
    private int maxWorldRank;
    private final int yearOfData, worldRank;
    private final Number value;
    private String description, suffix;
    private EventSources sources;

    public CountryAgricultureValue(int worldRank, int yearOfData, Number value) {
        this.worldRank = worldRank;
        this.yearOfData = yearOfData;
        this.value = value;
    }
    public CountryAgricultureValue(JSONObject json) {
        worldRank = json.getInt("worldRank");
        yearOfData = json.getInt("yearOfData");
        value = json.getNumber("value");
    }

    public int getMaxWorldRank() {
        return maxWorldRank;
    }
    public void setMaxWorldRank(int maxWorldRank) {
        this.maxWorldRank = maxWorldRank;
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

    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = LocalServer.fixEscapeValues(description);
    }

    public String getSuffix() {
        return suffix;
    }
    public void setSuffix(String suffix) {
        this.suffix = LocalServer.fixEscapeValues(suffix);
    }

    public EventSources getSources() {
        return sources;
    }
    public void setSources(EventSources sources) {
        this.sources = sources;
    }

    @Override
    public String toString() {
        return "\"" + description + "\":{" +
                "\"maxWorldRank\":" + maxWorldRank + "," +
                "\"worldRank\":" + worldRank + "," +
                "\"yearOfData\":" + yearOfData + "," +
                "\"value\":" + value + "," +
                "\"valueType\":\"" + NumberType.INTEGER.name() + "\"," +
                "\"suffix\":\"" + suffix + "\"," +
                "\"sources\":" + sources.toString() +
                "}";
    }

    @Override
    public String toServerJSON() {
        return "{" +
                "\"country\":\"" + country + "\"," +
                "\"worldRank\":" + worldRank + "," +
                "\"yearOfData\":" + yearOfData + "," +
                "\"value\":" + value +
                "}";
    }
}
