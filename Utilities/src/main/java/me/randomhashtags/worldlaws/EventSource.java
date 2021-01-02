package me.randomhashtags.worldlaws;

public final class EventSource {
    private final String siteName, homepageURL;

    public EventSource(String siteName, String homepageURL) {
        this.siteName = LocalServer.fixEscapeValues(siteName);
        this.homepageURL = LocalServer.fixEscapeValues(homepageURL);
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
