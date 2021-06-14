package me.randomhashtags.worldlaws;

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
    }

    @Override
    public String[] getHomeRequests() {
        return new String[] {
        };
    }
}
