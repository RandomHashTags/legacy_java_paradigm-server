package me.randomhashtags.worldlaws.event;

public final class EventSource {
    private String siteName, homepageURL;

    public EventSource(String siteName, String homepageURL) {
        this.siteName = siteName;
        this.homepageURL = homepageURL;
    }

    public String getSiteName() {
        return siteName;
    }
    public String getHomepageURL() {
        return homepageURL;
    }

    @Override
    public String toString() {
        return "{" +
                "\"siteName\":\"" + siteName + "\"," +
                "\"homepageURL\":\"" + homepageURL + "\"" +
                "}";
    }
}
