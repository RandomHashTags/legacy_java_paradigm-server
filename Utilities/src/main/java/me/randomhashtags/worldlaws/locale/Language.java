package me.randomhashtags.worldlaws.locale;

public enum Language {
    ARABIC("ar"),
    AZERBAIJANI("az"),
    CHINESE("zh"),
    CZECH("cs"),
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
    PERSIAN("fa"),
    POLISH("pl"),
    PORTUGUESE("pt"),
    RUSSIAN("ru"),
    SLOVAK("sk"),
    SPANISH("es"),
    SWEDISH("sv"),
    TURKISH("tr"),
    UKRAINIAN("uk"),
    VIETNAMESE("vi"),
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
