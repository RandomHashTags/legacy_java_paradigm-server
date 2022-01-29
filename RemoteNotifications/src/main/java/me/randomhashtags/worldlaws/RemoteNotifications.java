package me.randomhashtags.worldlaws;

public final class RemoteNotifications implements WLServer {

    public static void main(String[] args) {
        new RemoteNotifications().tryLoading();
    }

    private void tryLoading() {
        //test();
        load();
    }

    private void test() {
    }

    @Override
    public void stop() {
        AppleNotifications.INSTANCE.save();
    }

    @Override
    public TargetServer getServer() {
        return TargetServer.REMOTE_NOTIFICATIONS;
    }

    @Override
    public String getServerResponse(APIVersion version, String target) {
        final String[] values = target.split("/");
        switch (values[0]) {
            case "register":
                String token = values[2];
                switch (values[1]) {
                    case "apple":
                        AppleNotifications.INSTANCE.register(token);
                        break;
                    case "google":
                        break;
                    default:
                        break;
                }
                break;
            case "unregister":
                token = values[2];
                switch (values[1]) {
                    case "apple":
                        AppleNotifications.INSTANCE.unregister(token);
                        break;
                    default:
                        break;
                }
                break;
            case "send":
                break;
            default:
                break;
        }
        return null;
    }
}
