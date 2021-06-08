package me.randomhashtags.worldlaws.info.rankings;

import me.randomhashtags.worldlaws.LocalServer;
import me.randomhashtags.worldlaws.NumberType;

public final class CountryRankingInfoValueOther {
    private final Number value;
    private final NumberType valueType;
    private final String description, suffix;

    public CountryRankingInfoValueOther(Number value, NumberType valueType, String description, String suffix) {
        this.value = value;
        this.valueType = valueType;
        this.description = LocalServer.fixEscapeValues(description);
        this.suffix = LocalServer.fixEscapeValues(suffix);
    }

    public Number getValue() {
        return value;
    }
    public NumberType getValueType() {
        return valueType;
    }
    public String getDescription() {
        return description;
    }
    public String getSuffix() {
        return suffix;
    }

    @Override
    public String toString() {
        return "\"" + description + "\":{" +
                (suffix != null ? "\"suffix\":\"" + suffix + "\"," : "") +
                "\"value\":" + getValue() + "," +
                "\"valueType\":\"" + valueType.name() + "\"" +
                "}";
    }
}
