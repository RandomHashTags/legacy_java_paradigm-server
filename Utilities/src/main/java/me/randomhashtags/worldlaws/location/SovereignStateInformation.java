package me.randomhashtags.worldlaws.location;

import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class SovereignStateInformation {

    private String json;

    public SovereignStateInformation(ConcurrentHashMap<InformationType, HashSet<String>> info) {
        updateJSON(info == null ? new ConcurrentHashMap<>() : info);
    }
    private void updateJSON(ConcurrentHashMap<InformationType, HashSet<String>> info) {
        final StringBuilder builder = new StringBuilder("{");
        boolean isFirst = true;
        for(Map.Entry<InformationType, HashSet<String>> entry : info.entrySet()) {
            final InformationType informationType = entry.getKey();
            final HashSet<String> hashset = entry.getValue();
            builder.append(isFirst ? "" : ",").append("\"").append(informationType.getName()).append("\":{");
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
