package me.randomhashtags.worldlaws;

public class News implements WLServer {

    private NewsService service;

    public static void main(String[] args) {
        new News();
    }

    private News() {
        service = NewsAPIDotOrg.INSTANCE;
        load();
    }

    @Override
    public TargetServer getServer() {
        return TargetServer.NEWS;
    }

    @Override
    public String getServerResponse(APIVersion version, String identifier, String target) {
        return service.getResponseJSON(target);
    }
}
