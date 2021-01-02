package me.randomhashtags.worldlaws;

public final class CountryFilter {
    private final String backendID, text;

    CountryFilter(String backendID, String text) {
        this.backendID = backendID;
        this.text = text;
    }

    public String getBackendID() {
        return backendID;
    }
    public String getText() {
        return text;
    }

    @Override
    public String toString() {
        return "{" +
                "\"backendID\":\"" + backendID + "\"," +
                "\"text\":\"" + text + "\"" +
                "}";
    }
}
