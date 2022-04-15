package me.randomhashtags.worldlaws;

import me.randomhashtags.worldlaws.locale.JSONObjectTranslatable;
import me.randomhashtags.worldlaws.request.ServerRequest;
import me.randomhashtags.worldlaws.request.ServerRequestType;
import me.randomhashtags.worldlaws.service.entertainment.TwitchClips;

public final class Services implements WLServer {

    public static void main(String[] args) {
        new Services();
    }

    private Services() {
        //test();
        load();
    }

    @Override
    public TargetServer getServer() {
        return TargetServer.SERVICES;
    }

    private void test() {
        final JSONObjectTranslatable string = TwitchClips.INSTANCE.getResponse("getAll".split("/"));
        WLLogger.logInfo("Services;test;string=" + string);
    }

    @Override
    public ServerRequestType[] getRequestTypes() {
        return ServerRequestTypeServices.values();
    }

    @Override
    public ServerRequest[] getHomeRequests() {
        return new ServerRequest[] {
                //"stock_market"
                new ServerRequest(ServerRequestTypeServices.TWITCH_CLIPS, "getAll")
        };
    }

    @Override
    public long getHomeResponseUpdateInterval() {
        registerFixedTimer(UpdateIntervals.Services.TWITCH_CLIPS, TwitchClips.INSTANCE::refresh);
        return UpdateIntervals.Services.HOME;
    }
}
