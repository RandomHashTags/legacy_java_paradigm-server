package me.randomhashtags.worldlaws.request;

public enum WLContentType {
    CSS,
    HTML,
    JSON,
    ;

    public String getIdentifier() {
        switch (this) {
            case CSS: return "text/css";
            case HTML: return "text/html";
            case JSON: return "application/json";
            default: return null;
        }
    }
    public static WLContentType valueOfInput(String input) {
        for(WLContentType contentType : values()) {
            if(contentType.name().equalsIgnoreCase(input)) {
                return contentType;
            }
        }
        return null;
    }
}
