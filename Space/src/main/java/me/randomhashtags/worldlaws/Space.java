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
    public void getServerResponse(APIVersion version, String target, CompletionHandler handler) {
        final String[] values = target.split("/");
        final String key = values[0];
        switch (key) {
            case "constellation":
                getConstellationResponse(version, target.substring(key.length()+1), handler);
                break;
            case "planet":
                getPlanetResponse(version, target.substring(key.length()+1), handler);
                break;
            default:
                break;
        }
    }

    @Override
    public String[] getHomeRequests() {
        return new String[] {
        };
    }

    private void getConstellationResponse(APIVersion version, String target, CompletionHandler handler) {
        final String[] values = target.split("/");
        final String key = values[0];
        switch (key) {
            default:
                Constellations.INSTANCE.getByName(values[1], handler);
                break;
        }
    }

    private void getPlanetResponse(APIVersion version, String target, CompletionHandler handler) {
        final String[] values = target.split("/");
        final String key = values[0];
        switch (key) {
            default:
                break;
        }
    }
}
