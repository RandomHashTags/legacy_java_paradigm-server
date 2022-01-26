package me.randomhashtags.worldlaws;

import me.randomhashtags.worldlaws.constellation.Constellations;

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
    public String getServerResponse(APIVersion version, String target) {
        final String[] values = target.split("/");
        final String key = values[0];
        switch (key) {
            case "constellation":
                return getConstellationResponse(version, target.substring(key.length()+1));
            case "planet":
                return getPlanetResponse(version, target.substring(key.length()+1));
            default:
                return null;
        }
    }

    @Override
    public String[] getHomeRequests() {
        return new String[] {
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
