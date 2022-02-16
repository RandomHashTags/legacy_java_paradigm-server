package me.randomhashtags.worldlaws;

import me.randomhashtags.worldlaws.request.ServerRequest;
import me.randomhashtags.worldlaws.request.server.ServerRequestTypeScience;
import me.randomhashtags.worldlaws.service.yearReview.ScienceYearReview;
import me.randomhashtags.worldlaws.space.constellation.Constellations;

import java.time.LocalDate;
import java.time.Month;

public final class Science implements WLServer {

    public static void main(String[] args) {
        new Science();
    }

    private Science() {
        test();
        //load();
    }

    private void test() {
        final String string = ScienceYearReview.INSTANCE.getTodayEventsFromThePast(LocalDate.of(2022, Month.JUNE, 27));
        WLLogger.logInfo("Science;test;string=" + string);
    }

    @Override
    public TargetServer getServer() {
        return TargetServer.SCIENCE;
    }

    @Override
    public String getServerResponse(APIVersion version, String identifier, ServerRequest request) {
        final ServerRequestTypeScience type = (ServerRequestTypeScience) request.getType();
        final String target = request.getTarget();
        switch (type) {
            case SPACE:
                return getSpaceResponse(version, target);
            default:
                return null;
        }
    }

    private String getSpaceResponse(APIVersion version, String target) {
        final String[] values = target.split("/");
        final String key = values[0];
        switch (key) {
            case "constellation":
                return getConstellationResponse(version, target.substring(key.length() + 1));
            case "planet":
                return getPlanetResponse(version, target.substring(key.length() + 1));
            default:
                return null;
        }
    }

    @Override
    public ServerRequest[] getHomeRequests() {
        return new ServerRequest[] {
        };
    }

    private String getConstellationResponse(APIVersion version, String target) {
        final String[] values = target.split("/");
        final String key = values[0];
        switch (key) {
            default:
                return Constellations.INSTANCE.getByName(values[1]);
        }
    }

    private String getPlanetResponse(APIVersion version, String target) {
        final String[] values = target.split("/");
        final String key = values[0];
        switch (key) {
            default:
                return null;
        }
    }
}
