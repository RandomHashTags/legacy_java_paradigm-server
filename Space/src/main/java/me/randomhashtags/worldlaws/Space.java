package me.randomhashtags.worldlaws;

import me.randomhashtags.worldlaws.constellation.Constellations;
import me.randomhashtags.worldlaws.request.ServerRequest;
import me.randomhashtags.worldlaws.request.server.ServerRequestTypeSpace;

public final class Space implements WLServer {

    public static void main(String[] args) {
        new Space();
    }

    private Space() {
        test();
        //load();
    }

    private void test() {
    }

    @Override
    public TargetServer getServer() {
        return TargetServer.SPACE;
    }

    @Override
    public String getServerResponse(APIVersion version, String identifier, ServerRequest request) {
        final ServerRequestTypeSpace type = (ServerRequestTypeSpace) request.getType();
        final String target = request.getTarget();
        switch (type) {
            case CONSTELLATION:
                return getConstellationResponse(version, target);
            case PLANET:
                return getPlanetResponse(version, target);
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
