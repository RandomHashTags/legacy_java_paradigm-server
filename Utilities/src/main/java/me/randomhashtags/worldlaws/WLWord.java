package me.randomhashtags.worldlaws;

public enum WLWord {
    MINOR("Minor"),
    ADULT("Adult"),
    MINORITY("Minority"),
    MAJORITY("Majority"),
    LEGAL_AGE("Legal age"),

    DRINKING_AGE("Drinking age"),

    BB_GUN("BB gun"),
    PUBLIC_PLACE("Public place"),

    FIREWORK("Firework"),

    ANIMAL("Animal"),
    PET("Pet"),
    SERVICE_ANIMAL("Service animal"),
    TORTURE("Torture"),

    ;

    private String name;
    WLWord(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
