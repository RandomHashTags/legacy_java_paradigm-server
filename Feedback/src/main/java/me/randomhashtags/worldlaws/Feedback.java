package me.randomhashtags.worldlaws;

import me.randomhashtags.worldlaws.request.ServerRequestType;

public final class Feedback implements WLServer, Jsonable {

    public static void main(String[] args) {
        new Feedback();
    }

    private Feedback() {
        //test();
        load();
    }

    @Override
    public TargetServer getServer() {
        return TargetServer.FEEDBACK;
    }

    private void test() {
    }

    @Override
    public ServerRequestType[] getRequestTypes() {
        return ServerRequestTypeFeedback.values();
    }
}