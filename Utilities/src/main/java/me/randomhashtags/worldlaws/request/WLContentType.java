package me.randomhashtags.worldlaws.request;

public enum WLContentType {
    HTML,
    JSON,
    ;

    public String getIdentifier() {
        switch (this) {
            case HTML: return "text/html";
            case JSON: return "application/json";
            default: return null;
        }
    }
}
