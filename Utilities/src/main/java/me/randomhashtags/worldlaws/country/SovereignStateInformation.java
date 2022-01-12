package me.randomhashtags.worldlaws.country;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class SovereignStateInformation extends ConcurrentHashMap<SovereignStateInformationType, HashSet<String>> {

    public SovereignStateInformation(JSONObject json) {
        for(String key : json.keySet()) {
            final SovereignStateInformationType type = SovereignStateInformationType.valueOf(key);
            final HashSet<String> values = new HashSet<>();
            final Object obj = json.get(key);
            if(obj instanceof JSONObject) {
                final JSONObject innerJSON = (JSONObject) obj;
                for(String jsonKey : innerJSON.keySet()) {
                    values.add("\"" + jsonKey + "\":" + innerJSON.getJSONObject(jsonKey).toString());
                }
            } else if(obj instanceof JSONArray) {
                final JSONArray array = (JSONArray) obj;
                values.add(array.toString());
            }
            if(!values.isEmpty()) {
                put(type, values);
            }
        }
    }
    public SovereignStateInformation(ConcurrentHashMap<SovereignStateInformationType, HashSet<String>> info) {
        if(info != null) {
            putAll(info);
        }
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder("{");
        boolean isFirst = true;
        for(Map.Entry<SovereignStateInformationType, HashSet<String>> entry : entrySet()) {
            final SovereignStateInformationType informationType = entry.getKey();
            final boolean isArray = informationType.isArray();
            final HashSet<String> hashset = entry.getValue();
            builder.append(isFirst ? "" : ",").append("\"").append(informationType.getName()).append("\":").append(isArray ? "[" : "{");
            boolean isFirstString = true;
            for(String string : hashset) {
                final String realString = string.startsWith("{") ? string.substring(1, string.length()-1) : string;
                builder.append(isFirstString ? "" : ",").append(realString);
                isFirstString = false;
            }
            isFirst = false;
            builder.append(isArray ? "]" : "}");
        }
        builder.append("}");
        return builder.toString();
    }
}
