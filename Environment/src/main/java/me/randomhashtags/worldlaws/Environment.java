package me.randomhashtags.worldlaws;

import me.randomhashtags.worldlaws.globalwarming.GlobalWarming;

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
    public String getServerResponse(APIVersion version, String target) {
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
    public String[] getHomeRequests() {
        return new String[] {
        };
    }
}
