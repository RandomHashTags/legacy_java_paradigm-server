package me.randomhashtags.worldlaws.request;

import java.util.HashMap;

public final class ServerRequest {
    private final ServerRequestType type;
    private final String target;
    private final HashMap<String, String> query;

    public ServerRequest(ServerRequestType type) {
        this(type, null);
    }
    public ServerRequest(ServerRequestType type, String target) {
        this(type, target, null);
    }
    public ServerRequest(ServerRequestType type, String target, HashMap<String, String> query) {
        this.type = type;
        if(target != null && target.contains("?")) {
            final String[] values = target.split("\\?");
            this.target = values[0];
            this.query = new HashMap<>();
        } else {
            this.target = target;
            this.query = query;
        }
    }

    public ServerRequestType getType() {
        return type;
    }
    public String getTarget() {
        return target;
    }
    public HashMap<String, String> getQuery() {
        return query;
    }

    public String getTotalPath() {
        return type.name().toLowerCase() + (target != null ? "/" + target : "");
    }
}
