package me.randomhashtags.worldlaws.info.agriculture;

import me.randomhashtags.worldlaws.locale.JSONObjectTranslatable;
import org.json.JSONObject;

public final class CountryAgricultureValue {

    public static CountryAgricultureValue parse(JSONObject json) {
        return new CountryAgricultureValue(json);
    }

    private final int yearOfData, worldRank;
    private final Number value;

    public CountryAgricultureValue(int worldRank, int yearOfData, Number value) {
        this.worldRank = worldRank;
        this.yearOfData = yearOfData;
        this.value = value;
    }
    private CountryAgricultureValue(JSONObject json) {
        worldRank = json.getInt("worldRank");
        yearOfData = json.getInt("yearOfData");
        value = json.getNumber("value");
    }

    public Number getValue() {
        return value;
    }

    public JSONObjectTranslatable toJSONObject() {
        final JSONObjectTranslatable json = new JSONObjectTranslatable("description");
        json.put("worldRank", worldRank);
        json.put("yearOfData", yearOfData);
        json.put("value", value);
        return json;
    }
}
