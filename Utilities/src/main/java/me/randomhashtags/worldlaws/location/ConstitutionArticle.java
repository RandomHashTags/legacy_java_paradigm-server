package me.randomhashtags.worldlaws.location;

import java.util.List;

public final class ConstitutionArticle {
    private final String title, description;
    private final List<ConstitutionSection> sections;

    public ConstitutionArticle(String title, List<ConstitutionSection> sections) {
        this.title = title;
        this.description = null;
        this.sections = sections;
    }
    public ConstitutionArticle(String title, String description) {
        this.title = title;
        this.description = description == null ? "null" : description;
        this.sections = null;
    }

    private String getSectionsString() {
        final StringBuilder builder = new StringBuilder("{");
        boolean isFirst = true;
        for(ConstitutionSection section : sections) {
            builder.append(isFirst ? "" : ",").append(section.toString());
            isFirst = false;
        }
        builder.append("}");
        return builder.toString();
    }

    @Override
    public String toString() {
        return "\"" + title + "\":{" +
                (description != null ? "\"description\":\"" + description + "\"" : "") +
                (sections != null ? "\"sections\":" + getSectionsString() : "") +
                "}";
    }
}
