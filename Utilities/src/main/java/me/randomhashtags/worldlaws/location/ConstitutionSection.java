package me.randomhashtags.worldlaws.location;

public final class ConstitutionSection {

    private final String title, description;

    public ConstitutionSection(String title, String description) {
        this.title = title;
        this.description = description;
    }

    @Override
    public String toString() {
        return "\"" + title + "\":\"" + description + "\"";
    }
}
