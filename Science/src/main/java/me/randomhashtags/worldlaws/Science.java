package me.randomhashtags.worldlaws;

import me.randomhashtags.worldlaws.request.ServerRequest;
import me.randomhashtags.worldlaws.request.ServerRequestType;

public final class Science implements WLServer {

    public static void main(String[] args) {
        new Science();
    }

    private Science() {
        test();
        //load();
    }

    private void test() {
        //final String string = ScienceYearReview.INSTANCE.getTodayEventsFromThePast(LocalDate.now());
        //WLLogger.logInfo("Science;test;string=" + string);
    }

    @Override
    public TargetServer getServer() {
        return TargetServer.SCIENCE;
    }

    @Override
    public ServerRequestType[] getRequestTypes() {
        return ServerRequestTypeScience.values();
    }


    @Override
    public ServerRequest[] getHomeRequests() {
        return new ServerRequest[] {
        };
    }


}
