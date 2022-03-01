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
            final String[] values = target.split("\\?")[1].split("&");
            this.target = values[0];
            final HashMap<String, String> realQuery = new HashMap<>();
            for(String string : values) {
                final String[] test = string.split("=");
                realQuery.put(test[0], test[1]);
            }
            this.query = realQuery;
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
