package me.randomhashtags.worldlaws.location;

import java.util.HashMap;

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
    public void setInfo(CountryInfo info, String value) {
        this.info.put(info, value);
    }
    private void updateJSON() {
        final StringBuilder builder = new StringBuilder("{");
        boolean isFirst = true;
        for(CountryInfo information : info.keySet()) {
            final String value = info.get(information);
            builder.append(isFirst ? "" : ",").append("\"").append(information.name().toLowerCase()).append("\":").append(value);
            isFirst = false;
        }
        builder.append("}");
        json = builder.toString();
    }

    @Override
    public String toString() {
        return json;
    }
}
