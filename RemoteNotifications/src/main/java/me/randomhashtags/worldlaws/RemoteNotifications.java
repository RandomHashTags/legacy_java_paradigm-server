package me.randomhashtags.worldlaws;

import me.randomhashtags.worldlaws.request.ServerRequest;
import me.randomhashtags.worldlaws.request.ServerRequestType;

public final class RemoteNotifications implements WLServer {

    private static final RemoteNotificationDeviceTokenController[] CONTROLLERS = new RemoteNotificationDeviceTokenController[] {
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
        WLLogger.logInfo("RemoteNotifications;test;string=" + ServerRequestTypeRemoteNotifications.getCategories().toString());
    }

    @Override
    public void stop() {
        saveDeviceTokens();
    }

    private void saveDeviceTokens() {
        for(RemoteNotificationDeviceTokenController controller : CONTROLLERS) {
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
    public long getHomeResponseUpdateInterval() {
        registerFixedTimer(UpdateIntervals.RemoteNotifications.SAVE_DEVICE_TOKENS, this::saveDeviceTokens);
        return 0;
    }

    @Override
    public ServerRequestType[] getRequestTypes() {
        return ServerRequestTypeRemoteNotifications.values();
    }
}
