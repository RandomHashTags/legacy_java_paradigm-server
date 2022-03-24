package me.randomhashtags.worldlaws.history;

import me.randomhashtags.worldlaws.locale.JSONArrayTranslatable;

import java.util.ArrayList;

public final class CountryHistorySection extends ArrayList<CountryHistoryEra> {

    public CountryHistorySection() {
        super();
    }

    public JSONArrayTranslatable toJSONObject() {
        final JSONArrayTranslatable json = new JSONArrayTranslatable();
        for(CountryHistoryEra era : this) {
            json.put(era.toJSONObject());
        }
        return json;
    }
}
