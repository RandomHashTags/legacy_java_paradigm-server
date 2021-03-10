package me.randomhashtags.worldlaws.info.rankings;

import me.randomhashtags.worldlaws.EventSources;
import me.randomhashtags.worldlaws.LocalServer;
import me.randomhashtags.worldlaws.NumberType;
import me.randomhashtags.worldlaws.ServerObject;
import org.json.JSONObject;

import java.util.List;

public final class CountryRankingInfoValue implements ServerObject {
    public String country;
    private final int defcon;
    private int maxWorldRank, yearOfData;
    private final int worldRank;
    private final Number value;
    private NumberType valueType;
    private final boolean isEstimate;
    private String description, suffix;
    private EventSources sources;
    private String otherValues;

    public CountryRankingInfoValue(int defcon, int worldRank, int yearOfData, Number value, boolean isEstimate) {
        this.defcon = defcon;
        this.worldRank = worldRank;
        this.yearOfData = yearOfData;
        this.value = value;
        this.isEstimate = isEstimate;
    }
    public CountryRankingInfoValue(JSONObject json) {
        country = json.getString("country");
        defcon = json.has("defcon") ? json.getInt("defcon") : -1;
        worldRank = json.getInt("worldRank");
        yearOfData = json.has("yearOfData") ? json.getInt("yearOfData") : -1;
        value = json.getNumber("value");
        isEstimate = json.has("isEstimate") && json.getBoolean("isEstimate");
        otherValues = json.has("otherValues") ? json.getJSONArray("otherValues").toString() : null;
    }

    public int getDefcon() {
        return defcon;
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
    public void setYearOfData(int yearOfData) {
        this.yearOfData = yearOfData;
    }

    public Number getValue() {
        return value;
    }

    public NumberType getValueType() {
        return valueType;
    }
    public void setValueType(NumberType valueType) {
        this.valueType = valueType;
    }

    public boolean isEstimate() {
        return isEstimate;
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
    public void setSources(EventSources sources) {
        this.sources = sources;
    }

    @Override
    public String toString() {
        return "{" +
                (defcon != -1 ? "\"defcon\":" + defcon + "," : "") +
                "\"maxWorldRank\":" + maxWorldRank + "," +
                "\"worldRank\":" + worldRank + "," +
                "\"yearOfData\":" + yearOfData + "," +
                "\"value\":" + value + "," +
                "\"valueType\":\"" + valueType.name() + "\"," +
                (isEstimate ? "\"isEstimate\":true," : "") +
                "\"description\":\"" + description + "\"," +
                (suffix != null ? "\"suffix\":\"" + suffix + "\"," : "") +
                (otherValues != null ? "\"otherValues\":" + otherValues + "," : "") +
                "\"sources\":" + sources.toString() +
                "}";
    }

    @Override
    public String toServerJSON() {
        return "{" +
                "\"country\":\"" + country + "\"," +
                (defcon != -1 ? "\"defcon\":" + defcon + "," : "") +
                "\"worldRank\":" + worldRank + "," +
                (yearOfData != -1 ? "\"yearOfData\":" + yearOfData + "," : "") +
                (isEstimate ? "\"isEstimate\":true," : "") +
                (otherValues != null ? "\"otherValues\":" + otherValues + "," : "") +
                "\"value\":" + value +
                "}";
    }
}
