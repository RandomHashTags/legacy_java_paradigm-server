package me.randomhashtags.worldlaws.location;

import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class CountryInformation {

    private String json;

    public CountryInformation(ConcurrentHashMap<CountryInformationType, HashSet<String>> info) {
        updateJSON(info == null ? new ConcurrentHashMap<>() : info);
    }
    private void updateJSON(ConcurrentHashMap<CountryInformationType, HashSet<String>> info) {
        final StringBuilder builder = new StringBuilder("{");
        boolean isFirst = true;
        for(Map.Entry<CountryInformationType, HashSet<String>> entry : info.entrySet()) {
            final CountryInformationType informationType = entry.getKey();
            final HashSet<String> hashset = entry.getValue();
            builder.append(isFirst ? "" : ",").append("\"").append(informationType.name()).append("\":{");
            boolean isFirstString = true;
            for(String string : hashset) {
                if(string != null && !string.equals("null")) {
                    final String realString = string.startsWith("{") ? string.substring(1, string.length()-1) : string;
                    builder.append(isFirstString ? "" : ",").append(realString);
                    isFirstString = false;
                }
            }
            isFirst = false;
            builder.append("}");
        }
        builder.append("}");
        json = builder.toString();
    }

    @Override
    public String toString() {
        return json;
    }
}
