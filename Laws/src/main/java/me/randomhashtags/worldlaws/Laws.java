package me.randomhashtags.worldlaws;

import me.randomhashtags.worldlaws.country.usa.state.recode.Minnesota;
import me.randomhashtags.worldlaws.locale.JSONObjectTranslatable;
import me.randomhashtags.worldlaws.request.ServerRequestType;
import me.randomhashtags.worldlaws.request.WLHttpHandler;

public final class Laws implements WLServer {
    public static final Laws INSTANCE = new Laws();

    public static void main(String[] args) {
        INSTANCE.init();
    }

    private void init() {
        //test();
        load();
    }

    @Override
    public TargetServer getServer() {
        return TargetServer.LAWS;
    }

    private void test() {
        final Minnesota minnesota = Minnesota.INSTANCE;
        final JSONObjectTranslatable string = minnesota.getIndexes();
        WLLogger.logInfo("Laws;test;string=" + string);
    }

    @Override
    public void load() {
        startServer();
    }

    @Override
    public WLHttpHandler getDefaultHandler() {
        return ServerRequestTypeLaws.getDefaultHandler();
    }

    @Override
    public ServerRequestType[] getRequestTypes() {
        return ServerRequestTypeLaws.values();
    }
}
