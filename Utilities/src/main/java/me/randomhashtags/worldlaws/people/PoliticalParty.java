package me.randomhashtags.worldlaws.people;

public enum PoliticalParty {
    DEMOCRATIC("D"),
    INDEPENDENT("I"),
    LIBERTARIAN("L"),
    REPUBLICAN("R"),

    UNKNOWN("?"),

    ;

    private String name, abbreviation;

    PoliticalParty(String abbreviation) {
        final String name = name();
        this.name = name.charAt(0) + name.substring(1).toLowerCase();
        this.abbreviation = abbreviation;
    }
    public String getName() {
        return name;
    }
    public String getAbbreviation() {
        return abbreviation;
    }

    public static PoliticalParty fromAbbreviation(String input) {
        for(PoliticalParty p : PoliticalParty.values()) {
            if(p.getAbbreviation().equalsIgnoreCase(input)) {
                return p;
            }
        }
        return null;
    }
}
