package me.randomhashtags.worldlaws;

import me.randomhashtags.worldlaws.globalwarming.GlobalWarming;
import me.randomhashtags.worldlaws.locale.JSONObjectTranslatable;
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
    public JSONObjectTranslatable getServerResponse(APIVersion version, String identifier, ServerRequest request) {
        final String target = request.getTarget();
        final String[] values = target.split("/");
        final String key = values[0];
        switch (key) {
            case "globalwarming":
                return GlobalWarming.INSTANCE.getResponse(version, target.substring(key.length()+1).split("/"));
            default:
                return null;
        }
    }

    @Override
    public ServerRequest[] getHomeRequests() {
        return new ServerRequest[] {
        };
    }

    @Override
    public ServerRequestType[] getRequestTypes() {
        return null;
    }
}
