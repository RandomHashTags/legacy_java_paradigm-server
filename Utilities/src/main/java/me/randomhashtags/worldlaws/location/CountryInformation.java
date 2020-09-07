package me.randomhashtags.worldlaws.location;

import java.util.HashMap;

public final class CountryInformation {

    private HashMap<CountryInfo, String> info;
    private String json;

    public HashMap<CountryInfo, String> getInfo() {
        return info;
    }
    public void setInfo(CountryInfo info, String value) {
        this.info.put(info, value);
    }
    private void updateJSON() {
        final StringBuilder builder = new StringBuilder("{");
        boolean isFirst = true;
        for(CountryInfo info : info.keySet()) {
            final String value = this.info.get(info);
            builder.append(isFirst ? "" : ",\"" + info.name().toLowerCase() + "\":\"").append(value).append("\"");
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
