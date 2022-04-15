package me.randomhashtags.worldlaws;

import me.randomhashtags.worldlaws.request.ServerRequest;
import me.randomhashtags.worldlaws.request.ServerRequestType;

public final class Environment implements WLServer {

    public static void main(String[] args) {
        new Environment();
    }

    private Environment() {
        test();
        //load();
    }

    private void test() {
    }

    @Override
    public TargetServer getServer() {
        return TargetServer.ENVIRONMENT;
    }

    @Override
    public ServerRequest[] getHomeRequests() {
        return new ServerRequest[] {
        };
    }

    @Override
    public ServerRequestType[] getRequestTypes() {
        return ServerRequestTypeEnvironment.values();
    }
}
