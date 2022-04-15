package me.randomhashtags.worldlaws;

import me.randomhashtags.worldlaws.request.ServerRequestType;

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

    /*@Override
    public JSONObjectTranslatable getServerResponse(APIVersion version, String identifier, ServerRequest request) {
        final String target = request.getTarget();
        return service.getResponseJSON(target);
    }*/

    @Override
    public ServerRequestType[] getRequestTypes() {
        return null;
    }
}
