package me.randomhashtags.worldlaws.recode;

import me.randomhashtags.worldlaws.EventSources;
import me.randomhashtags.worldlaws.LocalServer;

import java.util.List;

public final class TestStatute extends TestStatuteAbstract {

    private final String description;
    private final List<TestSubdivision> subdivisions;
    private final EventSources sources;

    public TestStatute(String title, String description, List<TestSubdivision> subdivisions, EventSources sources) {
        super(null, title);
        this.description = LocalServer.fixEscapeValues(description);
        this.subdivisions = subdivisions;
        this.sources = sources;
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
        return "{" +
                "\"title\":\"" + title + "\"," +
                (description != null ? "\"description\":\"" + description + "\"," : "") +
                (subdivisions != null && !subdivisions.isEmpty() ? "\"subdivisions\":" + getSubdivisionsJSON() + "," : "") +
                "\"sources\":" + sources.toJSONObject().toString() +
                "}";
    }
}
