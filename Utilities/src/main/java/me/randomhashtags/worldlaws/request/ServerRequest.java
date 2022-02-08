package me.randomhashtags.worldlaws.request;

public final class ServerRequest {
    private final ServerRequestType type;
    private final String target;

    public ServerRequest(ServerRequestType type) {
        this(type, null);
    }
    public ServerRequest(ServerRequestType type, String target) {
        this.type = type;
        this.target = target;
    }

    public ServerRequestType getType() {
        return type;
    }
    public String getTarget() {
        return target;
    }
}
