package me.randomhashtags.worldlaws.info.rankings;

import me.randomhashtags.worldlaws.EventSources;
import me.randomhashtags.worldlaws.LocalServer;
import me.randomhashtags.worldlaws.NumberType;

import java.util.List;

public final class CountryRankingInfoValue {
    protected String country;
    private final int defcon, maxWorldRank, yearOfData;
    protected int worldRank;
    private final Number value;
    private final NumberType valueType;
    private final boolean isEstimate;
    private final String description, suffix;
    private final EventSources sources;
    private String otherValues;

    public CountryRankingInfoValue(int defcon, String country, int maxWorldRank, int worldRank, int yearOfData, Number value, NumberType valueType, boolean isEstimate, String description, String suffix, EventSources sources) {
        this.defcon = defcon;
        this.country = country;
        this.maxWorldRank = maxWorldRank;
        this.worldRank = worldRank;
        this.yearOfData = yearOfData;
        this.value = value;
        this.valueType = valueType;
        this.isEstimate = isEstimate;
        this.description = LocalServer.fixEscapeValues(description);
        this.suffix = LocalServer.fixEscapeValues(suffix);
        this.sources = sources;
    }

    public int getDefcon() {
        return defcon;
    }
    public String getCountry() {
        return country;
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
    public String getOtherValues() {
        return otherValues;
    }
    public void setOtherValues(List<CountryRankingInfoValueOther> values) {
        final StringBuilder builder = new StringBuilder("[");
        boolean isFirst = true;
        for(CountryRankingInfoValueOther value : values) {
            builder.append(isFirst ? "" : ",").append(value.toString());
            isFirst = false;
        }
        otherValues = builder.append("]").toString();
    }
    public EventSources getSources() {
        return sources;
    }

    @Override
    public String toString() {
        return "{" +
                "\"defcon\":" + defcon + "," +
                "\"maxWorldRank\":" + maxWorldRank + "," +
                "\"worldRank\":" + worldRank + "," +
                "\"yearOfData\":" + yearOfData + "," +
                "\"value\":" + value + "," +
                "\"valueType\":\"" + valueType.name() + "\"," +
                "\"isEstimate\":" + isEstimate + "," +
                "\"description\":\"" + description + "\"," +
                (suffix != null ? "\"suffix\":\"" + suffix + "\"," : "") +
                (otherValues != null ? "\"otherValues\":" + otherValues + "," : "") +
                "\"sources\":" + sources.toString() +
                "}";
    }
}
