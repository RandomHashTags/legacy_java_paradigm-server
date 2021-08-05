package me.randomhashtags.worldlaws.country;

import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class SovereignStateInformation {

    private String json;

    public SovereignStateInformation(ConcurrentHashMap<SovereignStateInformationType, HashSet<String>> info) {
        updateJSON(info == null ? new ConcurrentHashMap<>() : info);
    }
    private void updateJSON(ConcurrentHashMap<SovereignStateInformationType, HashSet<String>> info) {
        final StringBuilder builder = new StringBuilder("{");
        boolean isFirst = true;
        for(Map.Entry<SovereignStateInformationType, HashSet<String>> entry : info.entrySet()) {
            final SovereignStateInformationType informationType = entry.getKey();
            final HashSet<String> hashset = entry.getValue();
            builder.append(isFirst ? "" : ",").append("\"").append(informationType.getName()).append("\":{");
            boolean isFirstString = true;
            for(String string : hashset) {
                final String realString = string.startsWith("{") ? string.substring(1, string.length()-1) : string;
                builder.append(isFirstString ? "" : ",").append(realString);
                isFirstString = false;
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
