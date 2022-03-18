package me.randomhashtags.worldlaws.info.rankings;

import me.randomhashtags.worldlaws.LocalServer;
import me.randomhashtags.worldlaws.locale.JSONObjectTranslatable;
import org.json.JSONObject;

public final class CountryRankingInfoValueOther {
    private final Number value;
    private final NumberType valueType;
    private final String description, suffix;

    public static CountryRankingInfoValueOther parse(JSONObject json) {
        return new CountryRankingInfoValueOther(json);
    }

    public CountryRankingInfoValueOther(Number value, NumberType valueType, String description, String suffix) {
        this.value = value;
        this.valueType = valueType;
        this.description = LocalServer.fixEscapeValues(description);
        this.suffix = LocalServer.fixEscapeValues(suffix);
    }
    private CountryRankingInfoValueOther(JSONObject json) {
        description = json.getString("description");
        suffix = json.optString("suffix", null);
        final Object valueObj = json.get("value");
        value = valueObj instanceof Number ? (Number) valueObj : null;
        valueType = NumberType.valueOf(json.getString("valueType"));
    }

    public JSONObjectTranslatable toJSONObject() {
        final JSONObjectTranslatable json = new JSONObjectTranslatable("description");
        json.put("description", description);
        if(suffix != null) {
            json.put("suffix", suffix);
        }
        json.put("value", value);
        json.put("valueType", valueType.name());
        return json;
    }
}
