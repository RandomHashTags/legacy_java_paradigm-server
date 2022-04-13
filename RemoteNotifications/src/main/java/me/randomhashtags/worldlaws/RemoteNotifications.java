package me.randomhashtags.worldlaws;

import me.randomhashtags.worldlaws.locale.JSONObjectTranslatable;
import me.randomhashtags.worldlaws.request.ServerRequest;
import me.randomhashtags.worldlaws.request.ServerRequestType;

import java.util.UUID;

public final class RemoteNotifications implements WLServer {

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
        AppleNotifications.INSTANCE.save();
    }

    @Override
    public TargetServer getServer() {
        return TargetServer.REMOTE_NOTIFICATIONS;
    }

    @Override
    public JSONObjectTranslatable getServerResponse(APIVersion version, String identifier, ServerRequest request) {
        return null;
    }


    @Override
    public ServerRequestType[] getRequestTypes() {
        return ServerRequestTypeRemoteNotifications.values();
    }
}
