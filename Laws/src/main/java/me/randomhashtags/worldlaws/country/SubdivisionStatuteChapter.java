package me.randomhashtags.worldlaws.country;

public final class SubdivisionStatuteChapter implements SubdivisionLegal {
    private String chapter, title;

    public SubdivisionStatuteChapter(String chapter) {
        this.chapter = chapter;
    }

    public String getChapter() {
        return chapter;
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
                "\"chapter\":\"" + chapter + "\"," +
                "\"title\":\"" + title + "\"" +
                "}";
    }
}
