package me.randomhashtags.worldlaws.service;

import me.randomhashtags.worldlaws.locale.JSONObjectTranslatable;
import org.json.JSONObject;

public final class CountryServiceJSONObject extends JSONObjectTranslatable {

    public static CountryServiceJSONObject parse(JSONObject json) {
        final JSONObjectTranslatable translatable = new JSONObjectTranslatable();
        for(String key : json.keySet()) {
            final JSONObject innerJSON = json.getJSONObject(key);
            final JSONObjectTranslatable innerTranslatable = new JSONObjectTranslatable();
            for(String innerKey : innerJSON.keySet()) {
                innerTranslatable.put(innerKey, innerJSON.get(innerKey));
                innerTranslatable.addTranslatedKey(innerKey);
            }
            translatable.put(key, innerTranslatable);
            translatable.addTranslatedKey(key);
        }
        return null;
    }

    public CountryServiceJSONObject(JSONObject json) {

    }
}
