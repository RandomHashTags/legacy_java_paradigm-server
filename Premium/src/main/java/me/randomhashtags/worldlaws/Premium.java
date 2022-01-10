package me.randomhashtags.worldlaws;

public final class Premium implements WLServer {

    public static void main(String[] args) {
        new Premium().load();
    }

    @Override
    public TargetServer getServer() {
        return TargetServer.PREMIUM;
    }

    @Override
    public void getServerResponse(APIVersion version, String target, CompletionHandler handler) {
        final String[] values = target.split("/");
        final String key = values[0];
        switch (key) {
            case "verify":
                Verification.INSTANCE.verifyApple(null, handler);
                break;
            default:
                handler.handleString(null);
                break;
        }
    }

    @Override
    public AutoUpdateSettings getAutoUpdateSettings() {
        return null;
    }

    @Override
    public String[] getHomeRequests() {
        return null;
    }
}
