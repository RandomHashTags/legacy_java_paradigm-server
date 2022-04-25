package me.randomhashtags.worldlaws;

import me.randomhashtags.worldlaws.request.ServerRequest;
import me.randomhashtags.worldlaws.request.ServerRequestType;

import java.util.UUID;

public final class RemoteNotifications implements WLServer {

    private static final DeviceTokenController[] CONTROLLERS = new DeviceTokenController[] {
            AppleNotifications.INSTANCE
    };

    public static void main(String[] args) {
        new RemoteNotifications().tryLoading();
    }

    private void tryLoading() {
        //test();
        load();
    }

    private void test() {
        final UUID uuid = UUID.randomUUID();
        WLLogger.logInfo("RemoteNotifications;uuid=" + uuid.toString());
    }

    @Override
    public void stop() {
        for(DeviceTokenController controller : CONTROLLERS) {
            controller.save();
        }
    }

    @Override
    public TargetServer getServer() {
        return TargetServer.REMOTE_NOTIFICATIONS;
    }

    @Override
    public ServerRequest[] getHomeRequests() {
        return new ServerRequest[] {
                new ServerRequest(ServerRequestTypeRemoteNotifications.CATEGORIES)
        };
    }

    @Override
    public ServerRequestType[] getRequestTypes() {
        return ServerRequestTypeRemoteNotifications.values();
    }
}
