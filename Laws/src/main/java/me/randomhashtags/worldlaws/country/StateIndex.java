package me.randomhashtags.worldlaws.country;

public final class StateIndex implements StateLegal {
    private String backendID, title;

    public StateIndex(String backendID) {
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
        return "{" +
                "\"backendID\":\"" + backendID + "\"," +
                "\"title\":\"" + title + "\"" +
                "}";
    }
}
