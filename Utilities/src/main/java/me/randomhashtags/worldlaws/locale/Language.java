package me.randomhashtags.worldlaws.locale;

public enum Language {
    ARABIC("ar"),
    AZERBAIJANI("az"),
    BENGALI("bn"),
    BULGARIAN("bg"),
    CHINESE("zh"),
    CZECH("cs"),
    DANISH("da"),
    DUTCH("nl"),
    ENGLISH("en"),
    FINNISH("fi"),
    FRENCH("fr"),
    GERMAN("de"),
    GREEK("el"),
    HINDI("hi"),
    HUNGARIAN("hu"),
    INDONESIAN("id"),
    IRISH("ga"),
    ITALIAN("it"),
    JAPANESE("ja"),
    KOREAN("ko"),
    LATIN("la"),
    PERSIAN("fa"),
    POLISH("pl"),
    PORTUGUESE("pt"),
    PUNJABI("pa"),
    RUSSIAN("ru"),
    SLOVAK("sk"),
    SPANISH("es"),
    SWAHILI("sw"),
    SWEDISH("sv"),
    TURKISH("tr"),
    UKRAINIAN("uk"),
    URDU("ur"),
    VIETNAMESE("vi"),
    WELSH("cy"),
    ;

    public static Language valueOfString(String string) {
        for(Language language : values()) {
            if(language.getID().equalsIgnoreCase(string)) {
                return language;
            }
        }
        return null;
    }

    private final String id;

    Language(String id) {
        this.id = id;
    }

    public String getID() {
        return id;
    }
}
