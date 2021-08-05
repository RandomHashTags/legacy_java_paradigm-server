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
    public void getServerResponse(APIVersion version, String target, CompletionHandler handler) {
        final String[] values = target.split("/");
        final String key = values[0];
        switch (key) {
            case "globalwarming":
                GlobalWarming.INSTANCE.getResponse(version, target.substring(key.length()+1), handler);
                break;
            default:
                break;
        }
    }

    @Override
    public String[] getHomeRequests() {
        return new String[] {
        };
    }

    @Override
    public AutoUpdateSettings getAutoUpdateSettings() {
        return null;
    }
}
