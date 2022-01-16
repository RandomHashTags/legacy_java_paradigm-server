package me.randomhashtags.worldlaws;

import me.randomhashtags.worldlaws.service.NewsAPIDotOrg;

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
    public String getServerResponse(APIVersion version, String target) {
        return service.getResponseJSON(target);
    }

    @Override
    public String[] getHomeRequests() {
        return null;
    }

    @Override
    public AutoUpdateSettings getAutoUpdateSettings() {
        return null;
    }
}
