package me.randomhashtags.worldlaws.info.rankings;

import me.randomhashtags.worldlaws.EventSources;
import me.randomhashtags.worldlaws.locale.JSONArrayTranslatable;
import me.randomhashtags.worldlaws.locale.JSONObjectTranslatable;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

public final class CountryRankingInfoValue {
    public String country;
    private final int defcon, yearOfData;
    private int worldRank;
    private final Number value;
    private final boolean isEstimate;
    private EventSources sources;
    private JSONArrayTranslatable otherValues;

    public static CountryRankingInfoValue parse(JSONObject json) {
        return new CountryRankingInfoValue(json);
    }

    public CountryRankingInfoValue(int defcon, int worldRank, int yearOfData, Number value, boolean isEstimate) {
        this.defcon = defcon;
        this.worldRank = worldRank;
        this.yearOfData = yearOfData;
        this.value = value;
        this.isEstimate = isEstimate;
    }
    private CountryRankingInfoValue(JSONObject json) {
        defcon = json.optInt("defcon", -1);
        worldRank = json.getInt("worldRank");
        yearOfData = json.optInt("yearOfData", -1);
        isEstimate = json.optBoolean("isEstimate");
        value = json.optNumber("value", -1);
        if(json.has("otherValues")) {
            otherValues = new JSONArrayTranslatable();
            final JSONArray array = json.getJSONArray("otherValues");
            for(Object arrayObj : array) {
                final JSONObject otherValueJSON = (JSONObject) arrayObj;
                final CountryRankingInfoValueOther otherValue = CountryRankingInfoValueOther.parse(otherValueJSON);
                otherValues.put(otherValue.toJSONObject());
            }
        }
    }

    public void setWorldRank(int worldRank) {
        this.worldRank = worldRank;
    }

    public Number getValue() {
        return value;
    }

    public void setOtherValues(List<CountryRankingInfoValueOther> values) {
        otherValues = new JSONArrayTranslatable();
        for(CountryRankingInfoValueOther value : values) {
            otherValues.put(value.toJSONObject());
        }
    }

    public EventSources getSources() {
        return sources;
    }
    public void setSources(EventSources sources) {
        this.sources = sources;
    }

    public JSONObjectTranslatable toJSONObject() {
        final JSONObjectTranslatable json = new JSONObjectTranslatable("otherValues");
        if(defcon != -1) {
            json.put("defcon", defcon);
        }
        if(yearOfData > 0) {
            json.put("yearOfData", yearOfData);
        }
        if(isEstimate) {
            json.put("isEstimate", true);
        }
        if(otherValues != null) {
            json.put("otherValues", otherValues);
        }
        json.put("value", value);
        json.put("worldRank", worldRank);
        return json;
    }
}
