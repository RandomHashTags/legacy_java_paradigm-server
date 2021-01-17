package me.randomhashtags.worldlaws;

import me.randomhashtags.worldlaws.info.service.NewsAPIDotOrg;

public class News implements RestAPI, DataValues {

    private NewsService service;

    public static void main(String[] args) {
        new News();
    }

    private News() {
        service = NewsAPIDotOrg.INSTANCE;

        LocalServer.start("News", WL_NEWS_PORT, new CompletionHandler() {
            @Override
            public void handleClient(WLClient client) {
                final String target = client.getTarget();
                getResponseJSON(target, new CompletionHandler() {
                    @Override
                    public void handle(Object object) {
                        final String string = object.toString();
                        client.sendResponse(string);
                    }
                });
            }
        });
    }
    private void getResponseJSON(String target, CompletionHandler handler) {
        service.getResponseJSON(target, handler);
    }
}
