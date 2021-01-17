package me.randomhashtags.worldlaws.location;

import java.util.HashMap;
import java.util.Map;

public final class CountryInformation {

    private final HashMap<CountryInfo, String> info;
    private String json;

    public CountryInformation(HashMap<CountryInfo, String> info) {
        this.info = info == null ? new HashMap<>() : info;
        updateJSON();
    }

    public HashMap<CountryInfo, String> getInfo() {
        return info;
    }
    private void updateJSON() {
        final StringBuilder builder = new StringBuilder("{");
        boolean isFirst = true;
        for(Map.Entry<CountryInfo, String> entry : info.entrySet()) {
            final CountryInfo information = entry.getKey();
            final String value = entry.getValue();
            if(value != null && !value.equals("null")) {
                builder.append(isFirst ? "" : ",").append("\"").append(information.name().toLowerCase()).append("\":").append(value);
                isFirst = false;
            }
        }
        builder.append("}");
        json = builder.toString();
    }

    @Override
    public String toString() {
        return json;
    }
}
