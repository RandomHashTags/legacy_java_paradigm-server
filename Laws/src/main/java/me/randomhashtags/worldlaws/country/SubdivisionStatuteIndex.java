package me.randomhashtags.worldlaws.country;

public final class SubdivisionStatuteIndex implements SubdivisionLegal {
    private final String backendID;
    private String title;

    public SubdivisionStatuteIndex(String backendID) {
        this.backendID = backendID;
    }

    public String getIndex() {
        return backendID;
    }
    @Override
    public String getTitle() {
        return title;
    }
    @Override
    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public String toString() {
        return "\"" + title + "\":{" +
                "\"backendID\":\"" + backendID + "\"" +
                "}";
    }
}
