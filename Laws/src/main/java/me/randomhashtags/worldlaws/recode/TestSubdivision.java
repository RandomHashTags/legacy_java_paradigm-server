package me.randomhashtags.worldlaws.recode;

import me.randomhashtags.worldlaws.LocalServer;

import java.util.ArrayList;
import java.util.List;

public final class TestSubdivision {
    private final String title;
    private String description;
    private List<TestSubdivision> subdivisions;

    public TestSubdivision(String title, String description) {
        this.title = LocalServer.fixEscapeValues(title);
        this.description = LocalServer.fixEscapeValues(description);
    }

    public String getTitle() {
        return title;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = LocalServer.fixEscapeValues(description);
    }
    public void addSubdivision(TestSubdivision subdivision) {
        if(subdivisions == null) {
            subdivisions = new ArrayList<>();
        }
        subdivisions.add(subdivision);
    }

    private String getSubdivisionsJSON() {
        final StringBuilder builder = new StringBuilder("{");
        boolean isFirst = true;
        for(TestSubdivision subdivision : subdivisions) {
            builder.append(isFirst ? "" : ",").append(subdivision.toString());
            isFirst = false;
        }
        builder.append("}");
        return builder.toString();
    }

    @Override
    public String toString() {
        return "\"" + title + "\":{" +
                (subdivisions != null ? "\"subdivisions\":" + getSubdivisionsJSON() + "," : "") +
                "\"description\":\"" + description + "\"" +
                "}";
    }
}
