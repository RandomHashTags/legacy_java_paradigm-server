package me.randomhashtags.worldlaws;

import me.randomhashtags.worldlaws.locale.JSONObjectTranslatable;
import me.randomhashtags.worldlaws.request.ServerRequest;
import me.randomhashtags.worldlaws.request.ServerRequestType;
import me.randomhashtags.worldlaws.request.WLHttpHandler;

public final class Premium implements WLServer {

    public static void main(String[] args) {
        new Premium().initialize();
    }

    private void initialize() {
        test();
        //load();
    }

    private void test() {
    }

    @Override
    public void load() {
        startServer();
    }

    @Override
    public TargetServer getServer() {
        return TargetServer.PREMIUM;
    }

    @Override
    public JSONObjectTranslatable getServerResponse(APIVersion version, String identifier, ServerRequest request) {
        return null;
    }

    @Override
    public ServerRequestType[] getRequestTypes() {
        return ServerRequestTypePremium.values();
    }

    @Override
    public WLHttpHandler getDefaultHandler() {
        return httpExchange -> {
            WLLogger.logInfo("Premium;getDefaultHandler;path=" + httpExchange.getPath());
            return null;
        };
    }
}
