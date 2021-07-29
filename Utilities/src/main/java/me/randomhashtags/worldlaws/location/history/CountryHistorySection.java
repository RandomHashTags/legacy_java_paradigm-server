package me.randomhashtags.worldlaws.location.history;

import me.randomhashtags.worldlaws.LocalServer;

import java.util.List;

public final class CountryHistorySection {
    private final String name;
    private final List<CountryHistoryEra> eras;

    public CountryHistorySection(String name, List<CountryHistoryEra> eras) {
        this.name = LocalServer.fixEscapeValues(name);
        this.eras = eras;
    }

    public void addEra(CountryHistoryEra era) {
        eras.add(era);
    }

    private String getErasString() {
        final StringBuilder builder = new StringBuilder("{");
        boolean isFirst = true;
        for(CountryHistoryEra era : eras) {
            builder.append(isFirst ? "" : ",").append(era.toString());
            isFirst = false;
        }
        builder.append("}");
        return builder.toString();
    }

    @Override
    public String toString() {
        return "\"" + name + "\":" + getErasString();
    }
}
